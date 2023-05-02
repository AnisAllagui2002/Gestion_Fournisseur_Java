import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class Fournisseur extends JFrame {
    private JLabel lblIdFournisseur, lblNomFournisseur, lblAdresse, lblTelephone, lblMail;
    private JTextField txtIdFournisseur, txtNomFournisseur, txtAdresse, txtTelephone, txtMail;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnRechercher, btnEnregistrer;
    private JTable tableFournisseur;
    private DefaultTableModel modelTable;
    private Connection connection;
    private Statement statement;

    public Fournisseur() {
        // initialisation des composants
        lblIdFournisseur = new JLabel("ID fournisseur:");
        lblNomFournisseur = new JLabel("Nom fournisseur:");
        lblAdresse = new JLabel("Adresse:");
        lblTelephone = new JLabel("Téléphone:");
        lblMail = new JLabel("Email:");

        txtIdFournisseur = new JTextField(10);
        txtNomFournisseur = new JTextField(30);
        txtAdresse = new JTextField(30);
        txtTelephone = new JTextField(15);
        txtMail = new JTextField(30);

        btnAjouter = new JButton("Ajouter");
        btnModifier = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        btnRechercher = new JButton("Rechercher");
        btnEnregistrer = new JButton("Enregistrer");

        // ajout des composants au conteneur principal
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        JPanel panelInputs = new JPanel();
        panelInputs.setLayout(new GridLayout(5, 3));
        panelInputs.add(lblIdFournisseur);
        panelInputs.add(txtIdFournisseur);
        panelInputs.add(lblNomFournisseur);
        panelInputs.add(txtNomFournisseur);
        panelInputs.add(lblAdresse);
        panelInputs.add(txtAdresse);
        panelInputs.add(lblTelephone);
        panelInputs.add(txtTelephone);
        panelInputs.add(lblMail);
        panelInputs.add(txtMail);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new FlowLayout());
        panelButtons.add(btnAjouter);
        panelButtons.add(btnModifier);
        panelButtons.add(btnSupprimer);
        panelButtons.add(btnRechercher);
        panelButtons.add(btnEnregistrer);

        modelTable = new DefaultTableModel();
        tableFournisseur = new JTable(modelTable);
        modelTable.addColumn("ID fournisseur");
        modelTable.addColumn("Nom fournisseur");
        modelTable.addColumn("Adresse");
        modelTable.addColumn("Téléphone");
        modelTable.addColumn("Email");

        JScrollPane scrollPane = new JScrollPane(tableFournisseur);

        container.add(panelInputs, BorderLayout.NORTH);
        container.add(panelButtons, BorderLayout.SOUTH);
        container.add(scrollPane, BorderLayout.CENTER);

        // connexion à la base de données Oracle
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@localhost:1523:xe";
            String username = "root";
            String password = "";
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
            System.out.println("Connexion à la base de données Oracle réussie !");
        } catch (ClassNotFoundException ex) {
            System.out.println("Erreur de chargement du pilote Oracle !");
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.out.println("Erreur de connexion à la base de données Oracle !");
            ex.printStackTrace();
        }

        // ajout des listeners aux boutons
        btnAjouter.addActionListener(e -> ajouterFournisseur());
        btnModifier.addActionListener(e -> modifierFournisseur());
        btnSupprimer.addActionListener(e -> supprimerFournisseur());
        btnRechercher.addActionListener(e -> rechercherFournisseur());
        btnEnregistrer.addActionListener(e -> enregistrerFournisseur());

        // configuration de la fenêtre principale
        setTitle("Gestion des fournisseurs");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // méthode pour ajouter un fournisseur dans la base de données Oracle
    private void ajouterFournisseur() {
        String idFournisseur = txtIdFournisseur.getText();
        String nomFournisseur = txtNomFournisseur.getText();
        String adresse = txtAdresse.getText();
        String telephone = txtTelephone.getText();
        String mail = txtMail.getText();
        if (idFournisseur.isEmpty() || nomFournisseur.isEmpty() || adresse.isEmpty() || telephone.isEmpty() || mail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs !", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            String sql = "INSERT INTO fournisseur (id_fournisseur, nom_fournisseur, adresse, telephone, mail) VALUES ('" + idFournisseur + "', '" + nomFournisseur + "', '" + adresse + "', '" + telephone + "', '" + mail + "')";
            int rowsAffected = statement.executeUpdate(sql);
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Le fournisseur a été ajouté avec succès !", "Information", JOptionPane.INFORMATION_MESSAGE);
                viderChampsSaisie();
                actualiserTableFournisseur();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du fournisseur !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du fournisseur !", "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // méthode pour modifier un fournisseur dans la base de données Oracle
    private void modifierFournisseur() {
        int rowIndex = tableFournisseur.getSelectedRow();
        if (rowIndex < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un fournisseur à modifier !", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String idFournisseur = modelTable.getValueAt(rowIndex, 0).toString();
        String nomFournisseur = txtNomFournisseur.getText();
        String adresse = txtAdresse.getText();
        String telephone = txtTelephone.getText();
        String mail = txtMail.getText();
        if (nomFournisseur.isEmpty() || adresse.isEmpty() || telephone.isEmpty() || mail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs !", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            String sql = "UPDATE fournisseur SET nom_fournisseur = '" + nomFournisseur + "', adresse= '" + adresse + "', telephone = '" + telephone + "', mail = '" + mail + "' WHERE id_fournisseur = '" + idFournisseur + "'";
            int rowsAffected = statement.executeUpdate(sql);
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Le fournisseur a été modifié avec succès !", "Information", JOptionPane.INFORMATION_MESSAGE);
                viderChampsSaisie();
                actualiserTableFournisseur();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification du fournisseur !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification du fournisseur !", "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // méthode pour supprimer un fournisseur de la base de données Oracle
    private void supprimerFournisseur() {
        int rowIndex = tableFournisseur.getSelectedRow();
        if (rowIndex < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un fournisseur à supprimer !", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String idFournisseur = modelTable.getValueAt(rowIndex, 0).toString();
        int option = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce fournisseur ?", "Confirmation de suppression", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM fournisseur WHERE id_fournisseur = '" + idFournisseur + "'";
                int rowsAffected = statement.executeUpdate(sql);
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Le fournisseur a été supprimé avec succès !", "Information", JOptionPane.INFORMATION_MESSAGE);
                    viderChampsSaisie();
                    actualiserTableFournisseur();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du fournisseur !", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du fournisseur !", "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // méthode pour rechercher un fournisseur dans la base de données Oracle
    private void rechercherFournisseur() {
        String idFournisseur = txtIdFournisseur.getText();
        String nomFournisseur = txtNomFournisseur.getText();
        String adresse = txtAdresse.getText();
        String telephone = txtTelephone.getText();
        String mail = txtMail.getText();
        try {
            String sql = "SELECT * FROM fournisseur WHERE id_fournisseur LIKE '%" + idFournisseur + "%' AND nom_fournisseur LIKE '%" + nomFournisseur + "%' AND adresse LIKE '%" + adresse + "%' AND telephone LIKE '%" + telephone + "%' AND mail LIKE '%" + mail + "%'";
            ResultSet rs = statement.executeQuery(sql);
            modelTable.setRowCount(0);
            while (rs.next()) {
                Object[] row = new Object[5];
                row[0] = rs.getString("id_fournisseur");
                row[1] = rs.getString("nom_fournisseur");
                row[2] = rs.getString("adresse");
                row[3] = rs.getString("telephone");
                row[4] = rs.getString("mail");
                modelTable.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la recherche des fournisseurs !", "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // méthode pour enregistrer les modifications dans la base de données Oracle
    private void enregistrerFournisseur() {
        try {
            connection.commit();
            JOptionPane.showMessageDialog(this, "Les modifications ont été enregistrées avec succès !", "Information", JOptionPane.INFORMATION_MESSAGE);
            actualiserTableFournisseur();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement des modifications !", "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // méthode pour vider les champs de saisie
    private void viderChampsSaisie() {
        txtIdFournisseur.setText("");
        txtNomFournisseur.setText("");
        txtAdresse.setText("");
        txtTelephone.setText("");
        txtMail.setText("");
    }

    // méthode pour actualiser la table des fournisseurs
    private void actualiserTableFournisseur() {
        try {
            String sql = "SELECT * FROM fournisseur ORDER BY id_fournisseur";
            ResultSet rs = statement.executeQuery(sql);
            modelTable.setRowCount(0);
            while (rs.next()) {
                Object[] row = new Object[5];
                row[0] = rs.getString("id_fournisseur");
                row[1] = rs.getString("nom_fournisseur");
                row[2] = rs.getString("adresse");
                row[3] = rs.getString("telephone");
                row[4] = rs.getString("mail");
                modelTable.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des fournisseurs depuis la base de données !", "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // méthode principale pour créer et afficher l'interface graphique
    public static void main(String[] args) {
        Fournisseur fenetre = new Fournisseur();
        fenetre.setVisible(true);
    }
}