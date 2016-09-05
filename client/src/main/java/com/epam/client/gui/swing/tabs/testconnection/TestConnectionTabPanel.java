package com.epam.client.gui.swing.tabs.testconnection;

import com.epam.client.gui.swing.async.Processor;
import com.epam.common.service.ServerInfoService;
import com.epam.client.util.SwingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;

/**
 * Created by Ahmed_Khan on 5/9/2016.
 */
@Component
public class TestConnectionTabPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(TestConnectionTabPanel.class);

    private Processor processor;
    private ServerInfoService serverInfoService;

    @Value("${testconnection.button.label}")
    private String buttonLabel;
    @Value("${testconnection.errordialog.title}")
    private String errorDialogTitle;
    @Value("${testconnection.errordialog.msg}")
    private String errorDialogMsg;
    @Value("${testconnection.success.msg}")
    private String successMsg;

    @Autowired
    public TestConnectionTabPanel(Processor processor, ServerInfoService serverInfoService) {
        this.processor = processor;
        this.serverInfoService = serverInfoService;
    }

    @PostConstruct
    public void postConstruct() {
        SwingUtil.loadLookAndFeel(logger);

        JButton button = new JButton(buttonLabel);

        button.addActionListener(e -> {
                    processor.execute(serverInfoService::getServerInfo,
                            serverInfoDto -> JOptionPane.showMessageDialog(this,
                                    String.format(successMsg, serverInfoDto.getIp(), serverInfoDto.getDate()), "",
                                    JOptionPane.INFORMATION_MESSAGE),
                            exceptionConsumer -> JOptionPane.showMessageDialog(this, errorDialogMsg,
                                    errorDialogTitle, JOptionPane.ERROR_MESSAGE));
                }
        );

        add(button);

    }
}
