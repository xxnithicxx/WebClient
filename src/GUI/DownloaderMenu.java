package GUI;

import Helper.OpenResponse;

import javax.swing.*;
import java.awt.*;

public class DownloaderMenu {
    private final DefaultListModel<String> listModel;
    private static DownloaderMenu instance;

    private DownloaderMenu() {
        JFrame jFrame = new JFrame("Downloaded");
        jFrame.setMinimumSize(new Dimension(250, 400));
        jFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                jFrame.dispose();
                MainMenu.setShowDownloaded(false);
            }
        });

        this.listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        list.setCellRenderer(getRenderer());

        list.addListSelectionListener(
                e -> {
                    if (!e.getValueIsAdjusting()) {
                        JList<String> source = (JList<String>) e.getSource();
                        String selected = source.getSelectedValue();

                        OpenResponse.showResponse(selected);
                    }
                }
        );


        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setToolTipText("Double click to open file");

        jFrame.add(listScroller);

        jFrame.setLocation(500, 300);
        jFrame.setVisible(true);
        jFrame.pack();
    }

    private ListCellRenderer<? super String> getRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                listCellRendererComponent.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
                return listCellRendererComponent;
            }
        };
    }

    public static DownloaderMenu getInstance() {
        if (instance == null) {
            instance = new DownloaderMenu();
        }

        return instance;
    }

    public void addLink(String link) {
        if (this.listModel.contains(link)) {
            return;
        }

        this.listModel.addElement(link);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DownloaderMenu::new);
    }
}
