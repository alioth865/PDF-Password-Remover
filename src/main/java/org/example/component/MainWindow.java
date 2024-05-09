package org.example.component;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.example.exception.FileWithoutPasswordException;
import org.example.util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class MainWindow {

    private int decrypted;
    private int encrypted;
    private JFrame frame;
    private JPanel optionJPanel;
    private JScrollPane tableJScrollPane;
    private TableModel tableModel;
    private JTable tabla;
    private JButton btnRuta;
    private JTextField txtRuta;
    private JLabel lblClave;
    private JTextField txtClave;
    private JButton btnDesencriptar;
    private JProgressBar progressBar;

    public MainWindow() {
        createMainFrame();
        createOptionPanel();
        createTable();
    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    private void createMainFrame() {
        frame = new JFrame("PDF  Password  Remover");
        frame.setBounds(100, 100, 653, 430);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void createOptionPanel() {
        optionJPanel = new JPanel();
        optionJPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.getContentPane().add(optionJPanel, BorderLayout.NORTH);
        {
            GridBagLayout gbl_panelOpciones = new GridBagLayout();
            gbl_panelOpciones.columnWidths = new int[]{164, 279, 0};
            gbl_panelOpciones.rowHeights = new int[]{30, 30, 0, 0};
            gbl_panelOpciones.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
            gbl_panelOpciones.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
            optionJPanel.setLayout(gbl_panelOpciones);
        }
        btnRuta = new JButton("Seleccionar archivo / carpeta");
        btnRuta.setHorizontalAlignment(SwingConstants.LEFT);
        btnRuta.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFrame ventanaChooser = new JFrame();
                ventanaChooser.setBounds(100, 100, 450, 300);
                ventanaChooser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                ventanaChooser.getContentPane().setLayout(null);

                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Elige carpeta");
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                chooser.setAcceptAllFileFilterUsed(false);
                if (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION)
                    txtRuta.setText(chooser.getSelectedFile().toString());
                ventanaChooser.getContentPane().add(chooser);
            }
        });
        GridBagConstraints gbc_btnRuta = new GridBagConstraints();
        gbc_btnRuta.insets = new Insets(0, 0, 5, 5);
        gbc_btnRuta.gridx = 0;
        gbc_btnRuta.gridy = 0;
        optionJPanel.add(btnRuta, gbc_btnRuta);
        {
            txtRuta = new JTextField();
            txtRuta.setEditable(false);
            txtRuta.setHorizontalAlignment(SwingConstants.CENTER);
            GridBagConstraints gbc_txtRuta = new GridBagConstraints();
            gbc_txtRuta.insets = new Insets(0, 0, 5, 0);
            gbc_txtRuta.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtRuta.gridx = 1;
            gbc_txtRuta.gridy = 0;
            optionJPanel.add(txtRuta, gbc_txtRuta);
            txtRuta.setColumns(8);
        }
        {
            lblClave = new JLabel("Clave");
            GridBagConstraints gbc_lblClave = new GridBagConstraints();
            gbc_lblClave.insets = new Insets(0, 0, 5, 5);
            gbc_lblClave.gridx = 0;
            gbc_lblClave.gridy = 1;
            optionJPanel.add(lblClave, gbc_lblClave);
        }
        {
            txtClave = new JTextField();
            GridBagConstraints gbc_txtClave = new GridBagConstraints();
            gbc_txtClave.insets = new Insets(0, 0, 5, 0);
            gbc_txtClave.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtClave.gridx = 1;
            gbc_txtClave.gridy = 1;
            optionJPanel.add(txtClave, gbc_txtClave);
            txtClave.setColumns(10);
        }
        {
            btnDesencriptar = new JButton("Desencriptar");
            btnDesencriptar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    tabla.setAutoCreateRowSorter(true);
                    decrypted = 0;
                    encrypted = 0;
                    limpiarTabla();
                    habilitarGUI(false);
                    progressBar.setString("Analizando archivos...");
                    progressBar.setIndeterminate(true);
                    new Thread() {
                        public void run() {
                            String path = txtRuta.getText();
                            String password = txtClave.getText();
                            try {
                                File file = new File(path);
                                if (file.isDirectory()) {
                                    inspectFolder(new File(path), password);
                                } else {
                                    deletePassword(file, password);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            progressBar.setIndeterminate(false);
                            String resultado;
                            if (encrypted == 0) resultado = "Finalizado. No se encontraron PDF's encriptados";
                            else if (decrypted == 0)
                                resultado = "Finalizado. No se ha podido desencriptar ningun PDF.";
                            else resultado = "Finalizado. Se han descencriptado " + decrypted + " PDF's.";
                            progressBar.setString(resultado);
                            habilitarGUI(true);

                        }
                    }.start();
                }
            });
            GridBagConstraints gbc_btnDesencriptar = new GridBagConstraints();
            gbc_btnDesencriptar.gridwidth = 2;
            gbc_btnDesencriptar.fill = GridBagConstraints.HORIZONTAL;
            gbc_btnDesencriptar.gridx = 0;
            gbc_btnDesencriptar.gridy = 2;
            optionJPanel.add(btnDesencriptar, gbc_btnDesencriptar);
        }
    }

    public void habilitarGUI(boolean estado) {
        btnDesencriptar.setEnabled(estado);
        btnRuta.setEnabled(estado);
        txtClave.setEnabled(estado);
    }

    public void limpiarTabla() {
        while (tableModel.getRowCount() > 0) tableModel.removeRow(0);
    }


    public void createTable() {
        tableJScrollPane = new JScrollPane();
        frame.getContentPane().add(tableJScrollPane, BorderLayout.CENTER);
        tableModel = new TableModel();
        tabla = new JTable();
        tableJScrollPane.setViewportView(tabla);
        tabla.setModel(tableModel);
        tabla.setAutoCreateRowSorter(true);
        {
            progressBar = new JProgressBar();
            frame.getContentPane().add(progressBar, BorderLayout.SOUTH);
            progressBar.setStringPainted(true);
            progressBar.setFont(new Font("Dialog", Font.BOLD, 16));
            progressBar.setString("Esperando para desencriptar...");
        }

        // centrar todos los datos de la tabla
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tabla.setDefaultRenderer(String.class, centerRenderer);
        tabla.setDefaultRenderer(Integer.class, centerRenderer);

        // seteo tamanio de cada columna
        tabla.getColumnModel().getColumn(0).setPreferredWidth(30);
        tabla.getColumnModel().getColumn(0).setMaxWidth(44);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(300);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(2).setMaxWidth(130);
    }


    public void agregarFila(String archivo, String estado) {
        int fila = tabla.getRowCount();
        ((DefaultTableModel) tabla.getModel()).setRowCount(fila + 1); // creo nueva fila
        tabla.setValueAt(fila + 1, fila, 0);
        tabla.setValueAt(archivo, fila, 1);
        tabla.setValueAt(estado, fila, 2);
    }

    public void inspectFolder(File folder, String password) throws IOException {
        if (folder != null && folder.listFiles() != null) {
            for (File file : folder.listFiles()) {
                if (file.isDirectory()) {
                    inspectFolder(file, password);
                } else {
                    deletePassword(file, password);
                }
            }
        }

    }

    private void deletePassword(File file, String password) {
        if (Util.isPDFFile(file)) {
            try (PDDocument pdfDocument = Loader.loadPDF(new RandomAccessReadBufferedFile(file.getAbsolutePath()), password)) {
                if (pdfDocument.isEncrypted()) {
                    encrypted++;
                    deletePassword(pdfDocument, file.getAbsolutePath());
                }
            } catch (IOException | FileWithoutPasswordException e) {
                e.printStackTrace();
                agregarFila(file.getName(), "Incorrecta");
                return;
            }
        }
    }


    public void deletePassword(PDDocument document, String absolutePath) throws IOException, FileWithoutPasswordException {
        if (!document.isEncrypted()) throw new FileWithoutPasswordException("Error: El file ya no tenia clave");
        else {
            document.setAllSecurityToBeRemoved(true);
            File outputFile = new File(absolutePath);
            document.save(outputFile);
            decrypted++;
            agregarFila(absolutePath, "Removida");
        }
    }
}
