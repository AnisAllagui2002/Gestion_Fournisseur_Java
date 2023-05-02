import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Produit extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JLabel lblCodeProduit, lblNumProduit, lblPrix, lblType;
    private JTextField txtCodeProduit, txtNumProduit, txtPrix, txtType;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnRechercher, btnEnregistrer;
    private JTable tableProduit;
    private DefaultTableModel modelTable;
    private Connection connection;
    private PreparedStatement statement;

    public Produit() {
        super("Gestion des produits");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        // initialisation des composants graphiques
        JPanel panelSaisie = new JPanel(new GridLayout(4, 2));
        lblCodeProduit = new JLabel("Code produit :");
        txtCodeProduit = new JTextField(20);
        lblNumProduit = new JLabel("Numéro produit :");
        txtNumProduit = new JTextField(20);
        lblPrix = new JLabel("Prix :");
        txtPrix = new JTextField(20);
        lblType = new JLabel("Type :");
        txtType = new JTextField(20);
        panelSaisie.add(lblCodeProduit);
        panelSaisie.add(txtCodeProduit);
        panelSaisie.add(lblNumProduit);
        panelSaisie.add(txtNumProduit);
        panelSaisie.add(lblPrix);
        panelSaisie.add(txtPrix);
        panelSaisie.add(lblType);
        panelSaisie.add(txtType);
        add(panelSaisie, BorderLayout.NORTH);

        JPanel panelBoutons = new JPanel(new GridLayout(1, 5));
        btnAjouter = new JButton("Ajouter");
        btnAjouter.addActionListener(this);
        panelBoutons.add(btnAjouter);
        btnModifier = new JButton("Modifier");
        btnModifier.addActionListener(this);
        panelBoutons.add(btnModifier);
        btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(this);
        panelBoutons.add(btnSupprimer);
        btnRechercher = new JButton("Rechercher");
        btnRechercher.addActionListener(this);
        panelBoutons.add(btnRechercher);
        btnEnregistrer = new JButton("Enregistrer");
        btnEnregistrer.addActionListener(this);
        panelBoutons.add(btnEnregistrer);
        add(panelBoutons, BorderLayout.CENTER);

        modelTable = new DefaultTableModel(new Object[]{"Code produit", "Numéro produit", "Prix", "Type"}, 0);
        tableProduit = new JTable(modelTable);
        JScrollPane scrollPane = new JScrollPane(tableProduit);
        add(scrollPane, BorderLayout.SOUTH);

        // connexion à la base de données Oracle
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1522:XE", "root", "");
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Erreur de connexion à la base de données : " + e.getMessage());
            System.exit(0);
        }

        // chargement des produits à partir de la base de données
        try {
            statement = connection.prepareStatement("SELECT * FROM produit");
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                String codeProduit = result.getString("code_produit");
                String numProduit = result.getString("num_produit");
                double prix = result.getDouble("prix");
                String type = result.getString("type");
                modelTable.addRow(new Object[]{codeProduit, numProduit, prix, type});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erreur lors du chargement des produits : " + e.getMessage());
            System.exit(0);
        }

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAjouter) {
            ajouterProduit();
        } else if (e.getSource() == btnModifier) {
            modifierProduit();
        } else if (e.getSource() == btnSupprimer) {
            supprimerProduit();
        } else if (e.getSource() == btnRechercher) {
            rechercherProduit();
        } else if (e.getSource() == btnEnregistrer) {
            enregistrerProduit();
        }
    }

    private void ajouterProduit() {
        String codeProduit = txtCodeProduit.getText();
        String numProduit = txtNumProduit.getText();
        double prix = Double.parseDouble(txtPrix.getText());
        String type = txtType.getText();

        // vérification des champs
        if (codeProduit.isEmpty() || numProduit.isEmpty() || txtPrix.getText().isEmpty() || type.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs.");
            return;
        }

        // insertion du produit dans la base de données
        try {
            statement = connection.prepareStatement("INSERT INTO produit(code_produit, num_produit, prix, type) VALUES (?, ?, ?, ?)");
            statement.setString(1, codeProduit);
            statement.setString(2, numProduit);
            statement.setDouble(3, prix);
            statement.setString(4, type);
            statement.executeUpdate();
            modelTable.addRow(new Object[]{codeProduit, numProduit, prix, type});
            JOptionPane.showMessageDialog(null, "Le produit a été ajouté avec succès.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erreur lors de l'ajout du produit : " + ex.getMessage());
        }
    }

    private void modifierProduit() {
        int selectedRow = tableProduit.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Veuillez sélectionner un produit à modifier.");
            return;
        }

        String codeProduit = txtCodeProduit.getText();
        String numProduit = txtNumProduit.getText();
        double prix = Double.parseDouble(txtPrix.getText());
        String type = txtType.getText();

        // vérification des champs
        if (codeProduit.isEmpty() || numProduit.isEmpty() || txtPrix.getText().isEmpty() || type.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs.");
            return;
        }

        // mise à jour du produit dans la base de données
        try {
            statement = connection.prepareStatement("UPDATE produit SET num_produit=?, prix=?, type=? WHERE code_produit=?");
            statement.setString(1, numProduit);
            statement.setDouble(2, prix);
            statement.setString(3, type);

            statement.setString(4, codeProduit);
            statement.executeUpdate();
            modelTable.setValueAt(numProduit, selectedRow, 1);
            modelTable.setValueAt(prix, selectedRow, 2);
            modelTable.setValueAt(type, selectedRow, 3);
            JOptionPane.showMessageDialog(null, "Le produit a été modifié avec succès.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erreur lors de la modification du produit : " + ex.getMessage());
        }
    }

    private void supprimerProduit() {
        int selectedRow = tableProduit.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Veuillez sélectionner un produit à supprimer.");
            return;
        }

        String codeProduit = txtCodeProduit.getText();

        // suppression du produit dans la base de données
        try {
            statement = connection.prepareStatement("DELETE FROM produit WHERE code_produit=?");
            statement.setString(1, codeProduit);
            statement.executeUpdate();
            modelTable.removeRow(selectedRow);
            JOptionPane.showMessageDialog(null, "Le produit a été supprimé avec succès.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erreur lors de la suppression du produit : " + ex.getMessage());
        }
    }

    private void rechercherProduit() {
        String codeProduit = JOptionPane.showInputDialog(null, "Entrez le code du produit à rechercher :");
        if (codeProduit == null || codeProduit.isEmpty()) {
            return;
        }

        // recherche du produit dans la base de données
        try {
            statement = connection.prepareStatement("SELECT * FROM produit WHERE code_produit=?");
            statement.setString(1, codeProduit);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                txtCodeProduit.setText(result.getString("code_produit"));
                txtNumProduit.setText(result.getString("num_produit"));
                txtPrix.setText(String.valueOf(result.getDouble("prix")));
                txtType.setText(result.getString("type"));
            } else {
                JOptionPane.showMessageDialog(null, "Le produit n'a pas été trouvé.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erreur lors de la recherche du produit : " + ex.getMessage());
        }
    }

    private void enregistrerProduit() {
        try {
            statement = connection.prepareStatement("SELECT * FROM produit");
            ResultSet result = statement.executeQuery();
            FileWriter writer = new FileWriter("produits.csv");
            writer.write("code_produit,num_produit,prix,type\n");
            while (result.next()) {
                String codeProduit = result.getString("code_produit");
                String numProduit = result.getString("num_produit");
                double prix = result.getDouble("prix");
                String type = result.getString("type");
                writer.write(codeProduit + "," + numProduit + "," + prix + "," + type + "\n");
            }
            writer.close();
            JOptionPane.showMessageDialog(null, "Les produits ont été enregistrés dans le fichier produits.csv.");
        } catch (IOException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erreur lors de l'enregistrement des produits : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Produit produit = new Produit();
            produit.setVisible(true);
        });
    }
}

