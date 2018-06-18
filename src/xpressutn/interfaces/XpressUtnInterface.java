package xpressutn.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface XpressUtnInterface
{
	public <T> T find(Class<T> dtoClass,Object id) throws SQLException;
	
	public <T> T findAll(Class<T> dtoClass);
	
	public <T> List<T> query(Class<T> dtoClass,String xql);
//	public static List<T> query(Class<T> dtoClass,String xql,Object ...args);
	
	public <T> T queryForSingleRow(Class<T> dtoClass,String xql);
//	public static <T> T queryForSingleRow(Class<T> dtoClass,String xql,Object ...args);
	
	// TODO: agregar parámetro String atts
	public <T> int insertIfNotExists(Object dto);
//	public static <T> int insertIfNotExists(Object dto,String ...atts);
	
	// TODO: agregar parámetro Object args
	public <T> int insertIfNotExists(Object dto,String xql);
//	public static <T> int insertIfNotExists(Object dto,String xql,Object ...args);
	
	public <T> int update(Class<T> dtoClass);
	
	public <T> int update(Class<T> dtoClass, String xql);
	
	public <T> int delete(Class<T> dtoClass, Integer id);
}
