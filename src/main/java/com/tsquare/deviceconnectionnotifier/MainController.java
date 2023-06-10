package com.tsquare.deviceconnectionnotifier;

import com.tsquare.deviceconnectionnotifier.Database.Fields;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import com.twilio.Twilio;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController {
    @FXML
    private TextField twilio_account_sid;
    @FXML
    private TextField twilio_auth_token;
    @FXML
    private TextField twilio_phone;
    @FXML
    private TextField phone;
    @FXML
    private TextArea ip_addresses;
    @FXML
    private Button monitor_button;
    private Timer monitorTimer;
    private TimerTask monitorTask;

    @FXML
    protected void initialize() throws SQLException {
        Fields fields = new Fields();

        ResultSet accountSid = fields.getField("twilio_account_sid");
        if (accountSid.next()) {
            twilio_account_sid.setText(accountSid.getString("field_value"));
        }

        ResultSet accountToken = fields.getField("twilio_auth_token");
        if (accountToken.next()) {
            twilio_auth_token.setText(accountToken.getString("field_value"));
        }

        ResultSet accountPhone = fields.getField("twilio_phone");
        if (accountPhone.next()) {
            twilio_phone.setText(accountPhone.getString("field_value"));
        }

        ResultSet toPhone =  fields.getField("phone");
        if (toPhone.next()) {
            phone.setText(toPhone.getString("field_value"));
        }

        ResultSet ipField = fields.getField("ip_addresses");
        if (ipField.next()) {
            ip_addresses.setText(ipField.getString("field_value"));
        }

        fields.close();
    }

    @FXML
    protected void toggleMonitoring() throws SQLException {
        if (monitorTask != null) {
            monitor_button.setText("Start Monitoring");

            twilio_account_sid.setDisable(false);
            twilio_auth_token.setDisable(false);
            twilio_phone.setDisable(false);
            phone.setDisable(false);
            ip_addresses.setDisable(false);

            monitorTask.cancel();

            monitorTask = null;

            monitorTimer.cancel();

            monitorTimer = null;

            return;
        }

        twilio_account_sid.setDisable(true);
        twilio_auth_token.setDisable(true);
        twilio_phone.setDisable(true);
        phone.setDisable(true);
        ip_addresses.setDisable(true);

        List<String> ips_not_responding = new ArrayList<>();

        Map<String, String> ip_timer_map = new HashMap<>();
        Map<String, Integer> ip_sent_count_map = new HashMap<>();

        String[] ips = ip_addresses.getText().split("\n");
        for (String ip: ips) {
            ip_timer_map.put(ip, "");
            ip_sent_count_map.put(ip, 0);
        }

        monitorTimer = new Timer();

        Twilio.init(
                twilio_account_sid.getText(),
                twilio_auth_token.getText()
        );

        monitorTask = new TimerTask() {
            @Override
            public void run() {
                for (String ip: ips) {
                    if (Objects.equals(ip, "")) {
                        continue;
                    }

                    try {
                        InetAddress address = InetAddress.getByName(ip);
                        boolean isConnected = address.isReachable(5000);

                        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                        if (isConnected) {
                            System.out.println(ip + " is up");

                            if (ips_not_responding.contains(ip)) {
                                Message.creator(
                                        new PhoneNumber(phone.getText()),
                                        new PhoneNumber(twilio_phone.getText()),
                                        ip + " is back online!"
                                ).create();
                            }

                            ips_not_responding.remove(ip);
                            ip_timer_map.put(ip, "");
                            ip_sent_count_map.put(ip, 0);
                        } else {
                            System.out.println(ip + " is down");

                            String lastSent = ip_timer_map.get(ip);
                            LocalDateTime timeNow = LocalDateTime.now();
                            if (
                                    Objects.equals(lastSent, "")
                                    || (LocalDateTime.parse(lastSent, dateFormat).isBefore(timeNow.minusMinutes(15)) && ip_sent_count_map.get(ip) < 3)
                            ) {
                                Message.creator(
                                        new PhoneNumber(phone.getText()),
                                        new PhoneNumber(twilio_phone.getText()),
                                        ip + " is offline!"
                                ).create();

                                ip_timer_map.put(ip, dateFormat.format(timeNow));
                                ip_sent_count_map.put(ip, ip_sent_count_map.get(ip) + 1);

                                if (!ips_not_responding.contains(ip)) {
                                    ips_not_responding.add(ip);
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        monitorTimer.schedule(monitorTask, 0, 15000);

        Fields fields = new Fields();

        ResultSet accountSid = fields.getField("twilio_account_sid");
        if (accountSid.next()) {
            fields.updateField("twilio_account_sid", twilio_account_sid.getText());
        } else {
            fields.insertField("twilio_account_sid", twilio_account_sid.getText());
        }

        ResultSet accountToken = fields.getField("twilio_auth_token");
        if (accountToken.next()) {
            fields.updateField("twilio_auth_token", twilio_auth_token.getText());
        } else {
            fields.insertField("twilio_auth_token", twilio_auth_token.getText());
        }

        ResultSet accountPhone = fields.getField("twilio_phone");
        if (accountPhone.next()) {
            fields.updateField("twilio_phone", twilio_phone.getText());
        } else {
            fields.insertField("twilio_phone", twilio_phone.getText());
        }

        ResultSet toPhone =  fields.getField("phone");
        if (toPhone.next()) {
            fields.updateField("phone", phone.getText());
        } else {
            fields.insertField("phone", phone.getText());
        }

        ResultSet ipField = fields.getField("ip_addresses");
        if (ipField.next()) {
            fields.updateField("ip_addresses", ip_addresses.getText());
        } else {
            fields.insertField("ip_addresses", ip_addresses.getText());
        }

        fields.close();

        monitor_button.setText("Stop Monitoring");
    }
}