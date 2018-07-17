package xpressutn.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD
{
	private static Connection con;

	public static Connection getConnection()
	{
		Properties prop=new Properties();
		InputStream input=null;
		if(con!=null)
		{
			return con;
		}
		else
		{
			createConnection(prop,input);
		}
		return con;
	}

	private static void createConnection(Properties prop, InputStream input)
	{
		try
		{
			input=new FileInputStream("db.properties");
			prop.load(input);

			String conexion_url=prop.getProperty("jdbc.connection.url").trim();
			String usuario_conexion=prop.getProperty("jdbc.connection.user").trim();
			String usuario_password=prop.getProperty("jdbc.connection.password").trim();

			con=DriverManager.getConnection(conexion_url,usuario_conexion,usuario_password);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(input!=null)
			{
				try
				{
					input.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void closeConnection(){
		try
		{
			con.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}
