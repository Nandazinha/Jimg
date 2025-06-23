import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
 
public class imgs {
    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new imgs().createAndShowGUI());
}


// Adiciona suporte para visualização em tela cheia e salvar imagem exibida
private boolean isFullScreen = false;
private JFrame fullScreenFrame = null;

private void toggleFullScreen(BufferedImage img) {
    if (!isFullScreen) {
        fullScreenFrame = new JFrame();
        fullScreenFrame.setUndecorated(true);
        fullScreenFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        JLabel fullScreenLabel = new JLabel(new ImageIcon(img.getScaledInstance(
            Toolkit.getDefaultToolkit().getScreenSize().width,
            Toolkit.getDefaultToolkit().getScreenSize().height,
            Image.SCALE_SMOOTH)));
        fullScreenLabel.setHorizontalAlignment(JLabel.CENTER);
        fullScreenLabel.setVerticalAlignment(JLabel.CENTER);
        fullScreenFrame.add(fullScreenLabel);
        fullScreenFrame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    fullScreenFrame.dispose();
                    isFullScreen = false;
                }
            }
        });
        fullScreenFrame.setVisible(true);
        isFullScreen = true;
    }
}

// Adiciona botão para salvar imagem exibida
private JButton saveButton;
private File currentImageFile = null;

private void setupSaveButton(JPanel parentPanel) {
    saveButton = new JButton("Salvar Imagem");
    saveButton.setEnabled(false);
    saveButton.addActionListener(e -> saveCurrentImage());
    parentPanel.add(saveButton, BorderLayout.SOUTH);
}

private void saveCurrentImage() {
    if (currentImageFile != null) {
        try {
            BufferedImage img = ImageIO.read(currentImageFile);
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("imagem_salva.png"));
            int result = chooser.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File saveFile = chooser.getSelectedFile();
                ImageIO.write(img, "png", saveFile);
                JOptionPane.showMessageDialog(null, "Imagem salva com sucesso!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar imagem.");
        }
    }
}
private JPanel thumbnailsPanel;
private JLabel imageLabel;
 
private void createAndShowGUI() {
    JFrame frame = new JFrame("Visualizador de Imagens");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
 
    JButton openButton = new JButton("Abrir Pasta de Imagens");
    thumbnailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JScrollPane scrollPane = new JScrollPane(thumbnailsPanel);
    imageLabel = new JLabel("", JLabel.CENTER);
 
    openButton.addActionListener(e -> openFolder());
 
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(openButton, BorderLayout.NORTH);
    topPanel.add(scrollPane, BorderLayout.CENTER);
 
    frame.add(topPanel, BorderLayout.NORTH);
    frame.add(imageLabel, BorderLayout.CENTER);
frame.getContentPane().setBackground(new Color(245, 245, 250));
topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
thumbnailsPanel.setBackground(new Color(235, 235, 245));
scrollPane.setPreferredSize(new Dimension(600, 120));
openButton.setBackground(new Color(70, 130, 180));
openButton.setForeground(Color.WHITE);
openButton.setFocusPainted(false);
openButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
imageLabel.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 200), 2));
imageLabel.setOpaque(true);
imageLabel.setBackground(Color.WHITE);
    frame.setSize(800, 600);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
}
 
private void openFolder() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int result = chooser.showOpenDialog(null);
    if (result == JFileChooser.APPROVE_OPTION) {
        File folder = chooser.getSelectedFile();
        showThumbnails(folder);
    }
}
 
private void showThumbnails(File folder) {
    thumbnailsPanel.removeAll();
    File[] files = folder.listFiles((dir, name) -> {
        String lower = name.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".gif") || lower.endsWith(".bmp");
    });
    if (files != null) {
        for (File file : files) {
            try {
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    ImageIcon icon = new ImageIcon(img.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
                    JLabel thumb = new JLabel(icon);
                    thumb.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
                    thumb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    thumb.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                            showLargeImage(file);
                        }
                    });
                    thumbnailsPanel.add(thumb);
                }
            } catch (Exception ex) {
                // Ignore invalid images
            }
        }
    }
    thumbnailsPanel.revalidate();
    thumbnailsPanel.repaint();
}
 
private void showLargeImage(File file) {
    try {
        BufferedImage img = ImageIO.read(file);
        if (img != null) {
            Image scaled = img.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
        }
    } catch (Exception ex) {
        imageLabel.setIcon(null);
    }
}  
}