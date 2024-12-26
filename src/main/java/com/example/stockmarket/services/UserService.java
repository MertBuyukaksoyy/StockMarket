package com.example.stockmarket.services;

import com.example.stockmarket.controller.NotificationController;
import com.example.stockmarket.dao.RoleRepo;
import com.example.stockmarket.dao.TransactionRepo;
import com.example.stockmarket.dao.UserRepo;
import com.example.stockmarket.dao.UserRoleRepo;
import com.example.stockmarket.entity.*;
import com.example.stockmarket.security.JwtUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.*;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserRoleRepo userRoleRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AdminService adminService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private NotificationController notificationController;

    public void register(String username, String password, String email) {
        User user = new User(username, password, email, LocalDateTime.now());
        userRepo.save(user);
        portfolioService.createPortfolio(user,null,0);
        balanceService.createBalance(user);
        Optional<Role> userRoleOpt = roleRepo.findByRoleName("user");
        if (userRoleOpt.isPresent()) {
            UserRole userRole = new UserRole(user, userRoleOpt.get());
            userRoleRepo.save(userRole);
        } else {
            throw new RuntimeException("user rolü bulunamadı!");
        }
    }

    public void addUser(String username, String password, String email, String roleName){
        User user = new User(username, password,email, LocalDateTime.now());
        userRepo.save(user);
        portfolioService.createPortfolio(user,null,0);
        balanceService.createBalance(user);
        Optional<Role> selectedRole = roleRepo.findByRoleName(roleName);
        if (selectedRole.isPresent()){
            UserRole userRole = new UserRole(user, selectedRole.get());
            userRoleRepo.save(userRole);
        }

    }
    public String authenticateUser(String username, String rawPassword) {
        User user = userRepo.findByUsername(username).orElse(null);
        if (user != null && user.getPassword().equals(rawPassword)) {
            LocalDateTime lastLoginTime = user.getLastLoginTime();
            if(lastLoginTime != null) {
                List<BalanceCard> newCards = adminService.getNewBalanceCardsSince(lastLoginTime);
                for (BalanceCard card : newCards) {
                    notificationController.sendNotificationNew(username, "New Balance Card Added: " + card.getCardCode());
                }
            }
            user.setLastLoginTime(LocalDateTime.now());
            userRepo.save(user);
            return jwtUtil.generateToken(username);
        }
        return null;
    }
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User cannot be found: " + username));
    }
    public User findById(int id) {
        return userRepo.findById(id).orElseThrow(() -> new RuntimeException("User cannot be found: " + id));
    }

    public void generateExcel(HttpServletResponse response, User user) throws Exception {
        List<Transactions> transactions = transactionRepo.findByUser(user);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Transaction History");

        HSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("Transaction Id");
        row.createCell(1).setCellValue("Stock Name");
        row.createCell(2).setCellValue("Stock Symbol");
        row.createCell(3).setCellValue("Quantity");
        row.createCell(4).setCellValue("Transaction Type");
        row.createCell(5).setCellValue("Commission");
        row.createCell(6).setCellValue("Amount");
        row.createCell(7).setCellValue("Time");

        int dataRowIndex = 1;
        for (Transactions transaction : transactions) {
            HSSFRow dataRow = sheet.createRow(dataRowIndex++);
            dataRow.createCell(0).setCellValue(transaction.getTransactionId());
            dataRow.createCell(1).setCellValue(transaction.getStock().getStockName());
            dataRow.createCell(2).setCellValue(transaction.getStock().getStockSymbol());
            dataRow.createCell(3).setCellValue(transaction.getQuantity());
            dataRow.createCell(4).setCellValue(transaction.getTransactionType() ? "Buy" : "Sell");
            dataRow.createCell(5).setCellValue(transaction.getComission().doubleValue());
            dataRow.createCell(6).setCellValue(transaction.getAmount().doubleValue());
            dataRow.createCell(7).setCellValue(transaction.getTimeStamp() != null ? transaction.getTimeStamp().toString():"No TimeStamp");
        }

        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=transaction_history.xls");

        ServletOutputStream ops = response.getOutputStream();
        workbook.write(ops);
        workbook.close();
        ops.close();
    }


}
