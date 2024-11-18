package bancario;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import bancario.base.Cuenta;

public class OperacionesBanco {
    private String decisionTxt;
    private int decision;
    private Banco banco = new Banco();
    private int numCuentas = 0;
    


    public OperacionesBanco() {
    }

    
    public void menuOpciones(){
        

        JOptionPane.showMessageDialog(null, "Bienvenido al Banco Victor Money que deseas hacer hoy?");
        decisionTxt= JOptionPane.showInputDialog("1: Agregar Cuenta \n" +
                        "2: Buscar cuenta\n" +
                        "3: Consignar\n" + 
                        "4: retirar dinero\n" + 
                        "5: consultar total de dinero del banco\n" + 
                        "6: consultar cliente con mayor dinero");
        decision = Integer.parseInt(decisionTxt);
        menu(decision);

    }
    

    private void menu(int num){

        String url = "jdbc:postgresql://localhost:5432/banco";
                    String user = "postgres";
                    String password = "1234";
      
                switch (num) {
                    case 1:
    
                    numCuentas += 1;
                    String numCuentasString = String.valueOf(numCuentas);
                    String nombreTitular = JOptionPane.showInputDialog("Ingresa el nombre del titular de la cuenta\n");
                    String cedulaTitular = JOptionPane.showInputDialog("Ingresa la cedula del titular de la cuenta\n");
                    String tipoCuenta = JOptionPane.showInputDialog("Ingresa el tipo de la cuenta, \n Ahorros\nCorriente\n");
                    String saldoInicialStr = JOptionPane.showInputDialog("Ingresa el saldo inicial de la cuenta \n");
                    double saldoInicial = Double.parseDouble(saldoInicialStr);
        
                    boolean seAgrego = banco.adicionarCuenta(numCuentasString, saldoInicial, tipoCuenta, cedulaTitular, nombreTitular);
                  

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                JOptionPane.showMessageDialog(null, "El cliente fue agregado con éxito");
              
            // AGREGA UN NUEVO CLIENTE
              String insertClienteSQL = ("INSERT INTO clientes(cedula_cliente, nombre_cliente) VALUES (?,?)");
                PreparedStatement statement = connection.prepareStatement(insertClienteSQL);
                statement.setString(1, cedulaTitular);
                statement.setString(2, nombreTitular);
                statement.executeUpdate();
          
                   
               
            } catch (SQLException e) {
                System.out.println("Error de conexión: " + e.getMessage());
            }

                break;

            
                
            
                case 2: //BUSCA LA CUENTA
    

                String cuentaABuscar = JOptionPane.showInputDialog("Ingresa el numero de la cuenta a buscar\n");
                    Cuenta busqueda = banco.buscarCuenta(cuentaABuscar);

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Conexión exitosa");
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM cuentas where numero_cuenta = ?");
            statement.setString(1, cuentaABuscar);
            ResultSet resultSetBuscarCuenta = statement.executeQuery();

            while (resultSetBuscarCuenta.next()) {
                // Acceder a los datos de cada fila
                
                String numeroCuenta = resultSetBuscarCuenta.getString("numero_cuenta");
                String idCuenta = resultSetBuscarCuenta.getString("id_cuenta");
                // Imprimir los resultados
                JOptionPane.showMessageDialog(null, "El número de cuenta es:   " + numeroCuenta +" y su ID es " + idCuenta );
            }
             
           
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }

    
                    break;



                case 3:  //CONSIGNAR DINERO METODO

            
                try (Connection connection = DriverManager.getConnection(url, user, password)) {
                    System.out.println("Conexión exitosa");
        
                    // Solicitar el número de cuenta
                    String cuentaConsignar = JOptionPane.showInputDialog("Ingresa el número de la cuenta a consignar:");
                    if (cuentaConsignar == null || cuentaConsignar.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El número de cuenta no puede estar vacío.");
                        return;
                    }
        
                    // Verificar si la cuenta existe
                    String sqlBusqueda = "SELECT saldo_cuenta FROM cuentas WHERE numero_cuenta = ?";
                    try (PreparedStatement statementBusqueda = connection.prepareStatement(sqlBusqueda)) {
                        statementBusqueda.setString(1, cuentaConsignar);
                        ResultSet resultSet = statementBusqueda.executeQuery();
        
                        if (!resultSet.next()) {
                            JOptionPane.showMessageDialog(null, "La cuenta no existe.");
                        } else {

                            // Solicitar la cantidad a consignar
                            String cantidadConsignarStr = JOptionPane.showInputDialog("Ingresa la cantidad a consignar:");
                            if (cantidadConsignarStr == null || cantidadConsignarStr.trim().isEmpty()) {
                                JOptionPane.showMessageDialog(null, "La cantidad a consignar no puede estar vacía.");
                                return;
                            }
        
                            try {
                                double cantidadConsignar = Double.parseDouble(cantidadConsignarStr);
                                if (cantidadConsignar <= 0) {
                                    JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a cero.");
                                    return;
                                }
        
                                // Actualizar el saldo
                                String sqlUpdate = "UPDATE cuentas SET saldo_cuenta = saldo_cuenta + ? WHERE numero_cuenta = ?";
                                try (PreparedStatement statementUpdate = connection.prepareStatement(sqlUpdate)) {
                                    statementUpdate.setDouble(1, cantidadConsignar);
                                    statementUpdate.setString(2, cuentaConsignar);
        
                                    int filasAfectadas = statementUpdate.executeUpdate();
                                    if (filasAfectadas > 0) {
                                        JOptionPane.showMessageDialog(null, "Consignación exitosa.");
                                    } else {
                                        JOptionPane.showMessageDialog(null, "No se pudo actualizar el saldo.");
                                    }
                                }
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(null, "La cantidad ingresada no es válida.");
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error en la conexión con la base de datos.");
                }







    break;

                case 4: // METODO PARA RETIRAR
                cuentaABuscar = JOptionPane.showInputDialog("Ingresa el numero de la cuenta a retirar\n");
                Cuenta busquedaRetiro = banco.buscarCuenta(cuentaABuscar);
             // Actualizar la base de datos
             try (Connection connection = DriverManager.getConnection(url, user, password)) {
                String updateSql = "UPDATE cuentas SET saldo_cuenta = ? WHERE numero_cuenta = ?";
                PreparedStatement statement = connection.prepareStatement(updateSql);
                statement.setDouble(1, busquedaRetiro.getSaldo());
                statement.setString(2, busquedaRetiro.getNumCuenta());
                statement.executeUpdate();

                JOptionPane.showMessageDialog(null, "Retiro realizado con éxito.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al actualizar la base de datos: " + e.getMessage());
            }
                if (busquedaRetiro == null) {
                    JOptionPane.showMessageDialog(null, "La cuenta no existe");
                } else {
                    String cantidadConsignarStr = JOptionPane.showInputDialog("Ingresa la cantidad a retirar");
                    double cantidadConsignar = Double.parseDouble(cantidadConsignarStr);
            
                    // Validaciones
                    if (cantidadConsignar <= 0) {
                        JOptionPane.showMessageDialog(null, "La cantidad a retirar debe ser positiva.");
                    } else if (cantidadConsignar > busquedaRetiro.getSaldo()) {
                        JOptionPane.showMessageDialog(null, "Saldo insuficiente.");
                    } else {
                        busquedaRetiro.retirar(cantidadConsignar);
            
                       
                    }
                }
                break;

                case 5: //
                double total = 0.0;

    try (Connection connection = DriverManager.getConnection(url, user, password)) {
        String sql = "SELECT (SUM(saldo_cuenta)) FROM cuentas";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            total = resultSet.getDouble("total_saldo"); 
            System.out.println("El total del dinero del banco es "+ total); 

        } else {
            System.err.println("No se encontraron registros en la tabla cuentas.");
        }
    } catch (SQLException e) {
        System.err.println("Error al consultar el total de dinero: " + e.getMessage());
    }


                    // double totalDineroBanco = banco.consultarTotalDinero();
                    // JOptionPane.showMessageDialog(null, "El total de dinero del banco es: "+ totalDineroBanco);
                    break;
                case 6:
                    String nombreMayorDinero = banco.consultarClienteMayorSaldo();
                    JOptionPane.showMessageDialog(null, "El cliente con mayor dinero es: "+ nombreMayorDinero);    
                
            default:
                break;
}
                }
        

            }