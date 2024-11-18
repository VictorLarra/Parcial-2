
package bancario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import bancario.base.Cliente;
import bancario.base.Cuenta;

public class Banco {
    String nombre;
    ArrayList<Cuenta> cuentas;

    public Banco() {
        this.cuentas = new ArrayList<>(4);
    }

    public Cuenta buscarCuenta(String numero) {
        // for(int i = 0; i < this.cuentas.size(); i++) {
        //     if (numero.equals(cuentas.get(i).getNumero())) {
        //         return this.cuentas.get(i);
        //     }
        // }

        for (Cuenta cuenta : this.cuentas) {
            if (cuenta.getNumero().equals(numero)) {
                return cuenta;
            }
        }
        return null;
    }

    public boolean adicionarCuenta(String numero, double saldoInicial, String tipo, String cedulaTitular, String nombreTitular) {
       
       
       String url = "jdbc:postgresql://localhost:5432/banco";
        String user = "postgres";
        String password = "1234";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Conexión exitosa");
            Statement statement = connection.createStatement();

            // boolean result = statement.execute("INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_cuenta) VALUES ('123456789', 'Ahorro', 1000) RETURNING id_cuenta");
            // System.out.println(result);

            // Cuenta cuenta = new Cuenta("Ahorro", "utyvdhb-lioj", 1000);
            // boolean result = cuenta.insertarCuenta(statement);
            // if (result) {
            //     System.out.println("Cuenta insertada correctamente");
            // } else {
            //     System.out.println("No se pudo insertar la cuenta");
            // }

            //AÑADIR UN NUEVO CLIENTE 
            ResultSet resultSetAddCliente = statement.executeQuery("SELECT * FROM cuentas");

            while (resultSetAddCliente.next()) {
                // Acceder a los datos de cada fila
                int idCuenta = resultSetAddCliente.getInt("id_cuenta");
                String numeroCuenta = resultSetAddCliente.getString("numero_cuenta");
                String tipoCuenta = resultSetAddCliente.getString("tipo_cuenta");
                double saldo = resultSetAddCliente.getDouble("saldo_cuenta");

                // Imprimir los resultados
                System.out.println("ID: " + idCuenta + ", Numero: " + numeroCuenta + ", Tipo: " + tipoCuenta + ", Saldo: " + saldo);
            }
             
           


        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
       
       
       
       
        Cliente cliente = new Cliente(cedulaTitular, nombreTitular);

        Cuenta cuentaBuscar = this.buscarCuenta(numero);
        if (cuentaBuscar == null) {
            Cuenta cuenta = new Cuenta(tipo, numero, saldoInicial, cliente);
            return cuentas.add(cuenta);
        } else {
            return false;
        }
    }

    public double consultarTotalDinero() {
        double total = 0;

        for (Cuenta cuenta : this.cuentas) {
            total += cuenta.consultarSaldo();
        }

        // for (int i = 0; i < this.cuentas.size(); i++) {
        //     total += cuentas.get(i).getSaldo();
        // }

        return total;
    }

    public String consultarClienteMayorSaldo() {
        double mayorSaldo = 0;
        String nombreTitular = "";

        for (Cuenta cuenta : this.cuentas) {
            if (cuenta.consultarSaldo() > mayorSaldo) {
                mayorSaldo = cuenta.consultarSaldo();
                nombreTitular = cuenta.getTitular().getNombre();
            }
        }
        return nombreTitular.isEmpty() ? "Nadie": nombreTitular;
    }
}
