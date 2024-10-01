import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class Swan{

    private JFrame frame;
    private JComboBox<String> selectionBox;
    private JList<String> jList;
    private JTextField stringField;
    private JScrollPane listScrollPane;
    private DefaultListModel<String> listModel;

    private JButton addButton;
    private JButton upButton;
    private JButton downButton;
    private JButton deleteButton;
    private JButton addListButton;
    private JButton deleteListButton;

    private int previousSelection;
    private String location;

    public static void main(String[] args){

        Swan obj = new Swan();

        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
            UIManager.put( "Button.arc", 999 );
        } catch (Exception e) {
            e.printStackTrace();
        }

        obj.fileLocation();

        obj.initSelectionBox();
        obj.loadList();

        int index = obj.selectionBox.getSelectedIndex();
        if(index >= 0){
            obj.loadContents(index);
        }

        obj.initGUI();

    }

    public void initGUI(){

        initMainComponents();

        initButtons();

        initListeners();

        initWindow();

        if(selectionBox.getItemCount() >= 1){
            addButton.setEnabled(true);
            deleteListButton.setEnabled(true);
        }

        frame.setVisible(true);

    }


    public void initMainComponents(){

        frame = new JFrame("Swan");
        frame.setResizable(false);
        frame.setSize(new Dimension(650, 450));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        ImageIcon exlogoIcon = new ImageIcon(getClass().getResource("/images/exlogo.png"));
        frame.setIconImage(exlogoIcon.getImage().getScaledInstance(50,50,Image.SCALE_SMOOTH));

        jList = new JList<>(listModel);
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList.setFont(new Font("Arial", Font.PLAIN, 17));
        listScrollPane = new JScrollPane(jList);

        stringField = new JTextField(20);
        stringField.setFont(new Font("Arial", Font.PLAIN, 17));
        stringField.setPreferredSize(new Dimension(100, 37));

    }


    public void initButtons(){

        addButton = new JButton("Add");
        addButton.setEnabled(false);
        addButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedIndex = jList.getSelectedIndex();
                String content = stringField.getText().trim();

                if (!content.isEmpty()) {

                    if(selectedIndex == -1){
                        add(content);
                    }
                    else{
                        add(content,selectedIndex);
                    }

                    stringField.setText("");

                }

            }


        });


        deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                delete(jList.getSelectedIndex());

            }

        });


        upButton = new JButton("Up");
        upButton.setEnabled(false);
        upButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                up(jList.getSelectedIndex());

            }

        });


        downButton = new JButton("Down");
        downButton.setEnabled(false);
        downButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                down(jList.getSelectedIndex());

            }

        });


        addListButton = new JButton("Add List");
        addListButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                addList();
                stringField.setText("");

            }

        });


        deleteListButton = new JButton("Delete List");
        deleteListButton.setEnabled(false);
        deleteListButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                deleteList();

            }

        });
    }


    public void initWindow(){

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel leftPanel = new JPanel(new BorderLayout(10,10));
        leftPanel.add(selectionBox, BorderLayout.NORTH);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        leftPanel.add(stringField, BorderLayout.SOUTH);


        JPanel rightPanel = new JPanel(new BorderLayout(10,10));

        JPanel rightTopPanel = new JPanel();
        rightTopPanel.setLayout(new GridLayout(1,2,10,10));
        rightTopPanel.add(addListButton, BorderLayout.WEST);
        rightTopPanel.add(deleteListButton, BorderLayout.EAST);
        rightTopPanel.setPreferredSize(new Dimension(110,40));

        JPanel rightBottomPanel = new JPanel();
        rightBottomPanel.setPreferredSize(new Dimension(100,85));
        rightBottomPanel.setLayout(new GridLayout(2,2,10,10));
        rightBottomPanel.add(addButton);
        rightBottomPanel.add(upButton);
        rightBottomPanel.add(deleteButton);
        rightBottomPanel.add(downButton);

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/logo.png"));
        JLabel image = new JLabel(new ImageIcon(logoIcon.getImage().getScaledInstance(150,150,Image.SCALE_SMOOTH)));

        rightPanel.add(rightTopPanel, BorderLayout.NORTH);
        rightPanel.add(image,BorderLayout.CENTER);
        rightPanel.add(rightBottomPanel,BorderLayout.SOUTH);

        rightPanel.setPreferredSize(new Dimension(190,370));

        panel.add(leftPanel,BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        frame.add(panel);

    }


    public void initListeners(){

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                if(previousSelection >= 0){
                    saveContents(previousSelection);
                }

                saveList();
                frame.dispose();
                System.exit(0);

            }

        });


        jList.addListSelectionListener(e -> {

            boolean itemSelected = !jList.isSelectionEmpty();

            deleteButton.setEnabled(itemSelected);
            upButton.setEnabled(itemSelected);
            downButton.setEnabled(itemSelected);

        });


        selectionBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selection = selectionBox.getSelectedIndex();
                boolean itemSelected = (selection >= 0);

                if(itemSelected){
                    saveContents(previousSelection);
                    listModel.clear();
                    loadContents(selection);
                    previousSelection = selection;
                }

                addButton.setEnabled(itemSelected);
                deleteListButton.setEnabled(itemSelected);

            }
        });


        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!jList.getBounds().contains(e.getPoint())) {
                    jList.clearSelection();
                }
            }
        });

    }


    public void fileLocation(){
        location = System.getProperty("user.home")+"/Documents/Swan/bin";
        File f  = new File(location);
        f.mkdirs();
    }


    public void initSelectionBox(){
        selectionBox = new JComboBox<String>();
        selectionBox.setPreferredSize(new Dimension(100, 42));
        selectionBox.setFont(new Font("Arial", Font.PLAIN, 17));
        listModel = new DefaultListModel<String>();
    }


    public void loadContents(int index){
        try {

            if(index >= 0){
                FileInputStream f = new FileInputStream(location+"/"+selectionBox.getItemAt(index)+".bin");
                ObjectInputStream i = new ObjectInputStream(f);

                DefaultListModel<String> temp;
                temp = (DefaultListModel<String>) i.readObject();

                for(int j=0;j<temp.size();j++){
                    listModel.addElement(temp.get(j));
                }

                i.close();
            }

        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void saveContents(int index){
        try {

            if(index >= 0){
                FileOutputStream f = new FileOutputStream(location+"/"+selectionBox.getItemAt(index)+".bin");
                ObjectOutputStream o = new ObjectOutputStream(f);

                o.writeObject(listModel);

                o.close();

            }

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void saveList(){
        try {

            FileOutputStream f = new FileOutputStream(location+"/main.bin");
            ObjectOutputStream o = new ObjectOutputStream(f);

            o.writeObject(selectionBox);

            o.close();

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void loadList(){

        try {

            FileInputStream f = new FileInputStream(location+"/main.bin");
            ObjectInputStream i = new ObjectInputStream(f);

            selectionBox = (JComboBox<String>) i.readObject();

            i.close();

            if(selectionBox.getItemCount() >= 1){
                selectionBox.setSelectedIndex(0);
                previousSelection = 0;
            }
            else{
                previousSelection = -1;
            }
            
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void add(String name){
        listModel.addElement(name);
    }

    public void add(String name,int option){
        listModel.add(option, name);
    }

    public void delete(int option){
        listModel.remove(option);
    }

    public void up(int option){
        if(option!=0){
            String temp;
            temp = listModel.get(option);
            listModel.set(option,listModel.get(option-1));
            listModel.set(option-1,temp);

            jList.setSelectedIndex(jList.getSelectedIndex() - 1);

        }
    }


    public void down(int option){

        if(option!=listModel.size()-1){
            String temp;
            temp = listModel.get(option);
            listModel.set(option,listModel.get(option+1));
            listModel.set(option+1,temp);

            jList.setSelectedIndex(jList.getSelectedIndex() + 1);

        }

    }

    public void addList(){

        String content = stringField.getText().trim();
        if (!content.isEmpty() && !content.equals("null")){

            File f = new File (location+"/"+content+".bin");
            selectionBox.addItem(content);
            selectionBox.setSelectedIndex(selectionBox.getItemCount()-1);
            listModel.clear();

        }

    }

    public void deleteList(){

        int index = selectionBox.getSelectedIndex();

        if(index >= 0){
            String content = selectionBox.getItemAt(index);

            if(selectionBox.getItemCount()-1 >= 1){
                selectionBox.setSelectedIndex(0);
            }
            else{
                listModel.clear();
                previousSelection = -1;
            }

            selectionBox.removeItemAt(index);
            File f = new File(location+"/"+content+".bin");
            f.delete();
        }

    }

}
