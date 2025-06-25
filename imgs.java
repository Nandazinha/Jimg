import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class imgs {
    // Variáveis para controle de tela cheia
    private boolean isFullScreen = false;
    private JFrame fullScreenFrame = null;

    public static void main(String[] args) {
        // Inicia a interface gráfica na thread de eventos do Swing
        SwingUtilities.invokeLater(() -> new imgs().createAndShowGUI());
    }

    // Método para alternar entre tela cheia e modo normal
    private void toggleFullScreen(BufferedImage img) {
        if (!isFullScreen) {
            fullScreenFrame = new JFrame();
            fullScreenFrame.setUndecorated(true); // Remove bordas da janela
            fullScreenFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximiza a janela

            // Cria um rótulo para exibir a imagem em tela cheia
            JLabel fullScreenLabel = new JLabel(new ImageIcon(img.getScaledInstance(
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height,
                Image.SCALE_SMOOTH)));
            fullScreenLabel.setHorizontalAlignment(JLabel.CENTER);
            fullScreenLabel.setVerticalAlignment(JLabel.CENTER);
            fullScreenFrame.add(fullScreenLabel);

            // Adiciona um listener para fechar a tela cheia ao pressionar ESC
            fullScreenFrame.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        fullScreenFrame.dispose(); // Fecha a janela de tela cheia
                        isFullScreen = false; // Atualiza o estado
                    }
                }
            });

            fullScreenFrame.setVisible(true); // Torna a janela visível
            isFullScreen = true; // Atualiza o estado
        }
    }

    private JPanel thumbnailsPanel; // Painel para miniaturas
    private JLabel imageLabel; // Rótulo para exibir a imagem

    // Cria e exibe a interface gráfica
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Visualizador de Imagens");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JButton openButton = new JButton("Abrir Pasta de Imagens");
        thumbnailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JScrollPane scrollPane = new JScrollPane(thumbnailsPanel);
        imageLabel = new JLabel("", JLabel.CENTER);

        // Ação para abrir a pasta de imagens
        openButton.addActionListener(e -> openFolder());

        // Painel superior para o botão e miniaturas
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(openButton, BorderLayout.NORTH);
        topPanel.add(scrollPane, BorderLayout.CENTER);

        // Adiciona componentes ao frame
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(imageLabel, BorderLayout.CENTER);
        frame.getContentPane().setBackground(new Color(245, 245, 250)); // Cor de fundo
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Espaçamento
        thumbnailsPanel.setBackground(new Color(235, 235, 245)); // Cor do painel de miniaturas
        scrollPane.setPreferredSize(new Dimension(600, 120)); // Tamanho do scroll
        openButton.setBackground(new Color(70, 130, 180)); // Cor do botão
        openButton.setForeground(Color.WHITE); // Cor do texto
        openButton.setFocusPainted(false); // Remove o foco visual
        openButton.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Fonte do botão
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 200), 2)); // Borda da imagem
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE); // Cor de fundo da imagem
        frame.setSize(800, 600); // Tamanho da janela
        frame.setLocationRelativeTo(null); // Centraliza a janela
        frame.setVisible(true); // Torna a janela visível
    }

    // Método para abrir uma pasta e exibir suas imagens
    private void openFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Apenas diretórios
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File folder = chooser.getSelectedFile();
            showThumbnails(folder); // Exibe as miniaturas das imagens
        }
    }

    // Exibe miniaturas das imagens na pasta selecionada
    private void showThumbnails(File folder) {
        thumbnailsPanel.removeAll(); // Limpa o painel de miniaturas
        File[] files = folder.listFiles((dir, name) -> {
            String lower = name.toLowerCase();
            // Filtra arquivos de imagem
            return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".gif") || lower.endsWith(".bmp");
        });
        if (files != null) {
            for (File file : files) {
                try {
                    BufferedImage img = ImageIO.read(file); // Lê a imagem
                    if (img != null) {
                        // Cria um ícone a partir da imagem
                        ImageIcon icon = new ImageIcon(img.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
                        JLabel thumb = new JLabel(icon);
                        thumb.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Espaçamento
                        thumb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de mão
                        thumb.addMouseListener(new MouseAdapter() {
                            public void mouseClicked(MouseEvent e) {
                                showLargeImage(file); // Exibe a imagem grande ao clicar
                            }
                        });
                        thumbnailsPanel.add(thumb); // Adiciona a miniatura ao painel
                    }
                } catch (Exception ex) {
                    // Ignora imagens inválidas
                }
            }
        }
        thumbnailsPanel.revalidate(); // Atualiza o painel
        thumbnailsPanel.repaint(); // Repaint do painel
    }

    // Exibe a imagem grande ao clicar na miniatura
    private void showLargeImage(File file) {
        try {
            BufferedImage img = ImageIO.read(file); // Lê a imagem
            if (img != null) {
                Image scaled = img.getScaledInstance(400, 400, Image.SCALE_SMOOTH); // Redimensiona a imagem
                imageLabel.setIcon(new ImageIcon(scaled)); // Define o ícone da imagem
            }
        } catch (Exception ex) {
            imageLabel.setIcon(null); // Limpa o ícone em caso de erro
        }
    }
}