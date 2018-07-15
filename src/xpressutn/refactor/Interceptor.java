package xpressutn.refactor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import xpressutn.annotations.OneToMany;
import xpressutn.utils.MetaData;

public class Interceptor implements MethodInterceptor
{

	private Object target;
	MetaData metaData;

	public Interceptor(Object target, MetaData metadataEntry){
		this.target=target;
		this.metaData = metadataEntry;
	}
	
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable
	{
		if(this.metaData.getOneToManyColumnsField().containsKey(method.getName().toLowerCase()))
		{
			System.out.println("método del proxy.\n");
			Field field=this.metaData.getOneToManyColumnsField().get(method.getName().toLowerCase());
			Class clase=getClassParametized(field);

			String xql=generateQuerySuperPowa(method);
			System.out.println("PSEUDO QUERY GENERADA QUE SE LE PASA AL MÉTODO QUERY DE XPRESS");
			System.out.println(xql+"\n");
			
			Method setterMethod = getMethodSetter(method);
			Method getterMethodPK = getMethodPK();
			List objectList = XpressUTN.query(clase,xql,getterMethodPK.invoke(obj));
//			setterMethod.invoke(obj,objectList);
			return objectList;
		}
		System.out.println("método de la clase.");
		System.out.println(method.getName());
		return proxy.invoke(target,args);
	}
	
	private Method getMethodPK()
	{
		Method setterMethod = null;
		Field fieldPK = metaData.getPrimaryKeyField();
		for(Method metodo: this.metaData.getMetodos()){
			if(metodo.getName().equalsIgnoreCase("get"+fieldPK.getName())){
				setterMethod = metodo;
				break;
			}
		}
		return setterMethod;
	}

	private Method getMethodSetter(Method method)
	{
		String strMethod = method.getName().substring(3,method.getName().length());
		Method getterMethod = null;
		for(Method metodo: this.metaData.getMetodos()){
			if(metodo.getName().equalsIgnoreCase("set"+strMethod)){
				getterMethod = metodo;
				break;
			}
		}
		return getterMethod;
	}

	private String generateQuerySuperPowa(Method method)
	{
		Field field=this.metaData.getOneToManyColumnsField().get(method.getName().toLowerCase());
		Class clase=getClassParametized(field);
		Annotation anotacionObtenida=field.getAnnotation(OneToMany.class);
		String queryStr="SELECT * FROM "+clase.getSimpleName()+" x WHERE x."+((OneToMany)anotacionObtenida).mappedBy()+" = ?";
		return queryStr;
	}

	private Class getClassParametized(Field field)
	{
		ParameterizedType stringListType=(ParameterizedType)field.getGenericType();
		Class<?> stringListClass=(Class<?>)stringListType.getActualTypeArguments()[0];
		return stringListClass;
	}


}
