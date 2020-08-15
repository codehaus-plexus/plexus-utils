package org.codehaus.plexus.util.reflection;

/*
 * Copyright The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class used to instantiate an object using reflection. This utility hides many of the gory details needed to
 * do this.
 *
 * @author John Casey
 */
public final class Reflector
{
    private static final String CONSTRUCTOR_METHOD_NAME = "$$CONSTRUCTOR$$";

    private static final String GET_INSTANCE_METHOD_NAME = "getInstance";

    private Map<String, Map<String, Map<String, Method>>> classMaps =
        new HashMap<String, Map<String, Map<String, Method>>>();

    /** Ensure no instances of Reflector are created...this is a utility. */
    public Reflector()
    {
    }

    /**
     * Create a new instance of a class, given the array of parameters... Uses constructor caching to find a constructor
     * that matches the parameter types, either specifically (first choice) or abstractly...
     *
     * @param theClass The class to instantiate
     * @param params The parameters to pass to the constructor
     * @param <T> the type
     * @return The instantiated object
     * @throws ReflectorException In case anything goes wrong here...
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    public <T> T newInstance( Class<T> theClass, Object[] params )
        throws ReflectorException
    {
        if ( params == null )
        {
            params = new Object[0];
        }

        Class[] paramTypes = new Class[params.length];

        for ( int i = 0, len = params.length; i < len; i++ )
        {
            paramTypes[i] = params[i].getClass();
        }

        try
        {
            Constructor<T> con = getConstructor( theClass, paramTypes );

            if ( con == null )
            {
                StringBuilder buffer = new StringBuilder();

                buffer.append( "Constructor not found for class: " );
                buffer.append( theClass.getName() );
                buffer.append( " with specified or ancestor parameter classes: " );

                for ( Class paramType : paramTypes )
                {
                    buffer.append( paramType.getName() );
                    buffer.append( ',' );
                }

                buffer.setLength( buffer.length() - 1 );

                throw new ReflectorException( buffer.toString() );
            }

            return con.newInstance( params );
        }
        catch ( InstantiationException | InvocationTargetException | IllegalAccessException ex )
        {
            throw new ReflectorException( ex );
        }
    }

    /**
     * Retrieve the singleton instance of a class, given the array of parameters... Uses constructor caching to find a
     * constructor that matches the parameter types, either specifically (first choice) or abstractly...
     *
     * @param theClass The class to retrieve the singleton of
     * @param initParams The parameters to pass to the constructor
     * @param <T> the type
     * @return The singleton object
     * @throws ReflectorException In case anything goes wrong here...
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    public <T> T getSingleton( Class<T> theClass, Object[] initParams )
        throws ReflectorException
    {
        Class[] paramTypes = new Class[initParams.length];

        for ( int i = 0, len = initParams.length; i < len; i++ )
        {
            paramTypes[i] = initParams[i].getClass();
        }

        try
        {
            Method method = getMethod( theClass, GET_INSTANCE_METHOD_NAME, paramTypes );

            // noinspection unchecked
            return (T) method.invoke( null, initParams );
        }
        catch ( InvocationTargetException | IllegalAccessException ex )
        {
            throw new ReflectorException( ex );
        }
    }

    /**
     * Invoke the specified method on the specified target with the specified params...
     *
     * @param target The target of the invocation
     * @param methodName The method name to invoke
     * @param params The parameters to pass to the method invocation
     * @return The result of the method call
     * @throws ReflectorException In case of an error looking up or invoking the method.
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    public Object invoke( Object target, String methodName, Object[] params )
        throws ReflectorException
    {
        if ( params == null )
        {
            params = new Object[0];
        }

        Class[] paramTypes = new Class[params.length];

        for ( int i = 0, len = params.length; i < len; i++ )
        {
            paramTypes[i] = params[i].getClass();
        }

        try
        {
            Method method = getMethod( target.getClass(), methodName, paramTypes );

            if ( method == null )
            {
                StringBuilder buffer = new StringBuilder();

                buffer.append( "Singleton-producing method named '" ).append( methodName ).append( "' not found with specified parameter classes: " );

                for ( Class paramType : paramTypes )
                {
                    buffer.append( paramType.getName() );
                    buffer.append( ',' );
                }

                buffer.setLength( buffer.length() - 1 );

                throw new ReflectorException( buffer.toString() );
            }

            return method.invoke( target, params );
        }
        catch ( InvocationTargetException | IllegalAccessException ex )
        {
            throw new ReflectorException( ex );
        }
    }

    @SuppressWarnings( { "UnusedDeclaration" } )
    public Object getStaticField( Class targetClass, String fieldName )
        throws ReflectorException
    {
        try
        {
            Field field = targetClass.getField( fieldName );

            return field.get( null );
        }
        catch ( SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e )
        {
            throw new ReflectorException( e );
        }
    }

    @SuppressWarnings( { "UnusedDeclaration" } )
    public Object getField( Object target, String fieldName )
        throws ReflectorException
    {
        return getField( target, fieldName, false );
    }

    public Object getField( Object target, String fieldName, boolean breakAccessibility )
        throws ReflectorException
    {
        Class targetClass = target.getClass();
        while ( targetClass != null )
        {
            try
            {
                Field field = targetClass.getDeclaredField( fieldName );

                boolean accessibilityBroken = false;
                if ( !field.isAccessible() && breakAccessibility )
                {
                    field.setAccessible( true );
                    accessibilityBroken = true;
                }

                Object result = field.get( target );

                if ( accessibilityBroken )
                {
                    field.setAccessible( false );
                }

                return result;
            }
            catch ( SecurityException e )
            {
                throw new ReflectorException( e );
            }
            catch ( NoSuchFieldException e )
            {
                if ( targetClass == Object.class )
                    throw new ReflectorException( e );
                targetClass = targetClass.getSuperclass();
            }
            catch ( IllegalAccessException e )
            {
                throw new ReflectorException( e );
            }
        }
        // Never reached, but needed to satisfy compiler
        return null;
    }

    /**
     * Invoke the specified static method with the specified params...
     *
     * @param targetClass The target class of the invocation
     * @param methodName The method name to invoke
     * @param params The parameters to pass to the method invocation
     * @return The result of the method call
     * @throws ReflectorException In case of an error looking up or invoking the method.
     */
    @SuppressWarnings( { "UnusedDeclaration" } )
    public Object invokeStatic( Class targetClass, String methodName, Object[] params )
        throws ReflectorException
    {
        if ( params == null )
        {
            params = new Object[0];
        }

        Class[] paramTypes = new Class[params.length];

        for ( int i = 0, len = params.length; i < len; i++ )
        {
            paramTypes[i] = params[i].getClass();
        }

        try
        {
            Method method = getMethod( targetClass, methodName, paramTypes );

            if ( method == null )
            {
                StringBuilder buffer = new StringBuilder();

                buffer.append( "Singleton-producing method named \'" ).append( methodName ).append( "\' not found with specified parameter classes: " );

                for ( Class paramType : paramTypes )
                {
                    buffer.append( paramType.getName() );
                    buffer.append( ',' );
                }

                buffer.setLength( buffer.length() - 1 );

                throw new ReflectorException( buffer.toString() );
            }

            return method.invoke( null, params );
        }
        catch ( InvocationTargetException | IllegalAccessException ex )
        {
            throw new ReflectorException( ex );
        }
    }

    /**
     * Return the constructor, checking the cache first and storing in cache if not already there..
     *
     * @param targetClass The class to get the constructor from
     * @param params The classes of the parameters which the constructor should match.
     * @param <T> the type
     * @return the Constructor object that matches.
     * @throws ReflectorException In case we can't retrieve the proper constructor.
     */
    public <T> Constructor<T> getConstructor( Class<T> targetClass, Class[] params )
        throws ReflectorException
    {
        Map<String, Constructor<T>> constructorMap = getConstructorMap( targetClass );

        StringBuilder key = new StringBuilder( 200 );

        key.append( "(" );

        for ( Class param : params )
        {
            key.append( param.getName() );
            key.append( "," );
        }

        if ( params.length > 0 )
        {
            key.setLength( key.length() - 1 );
        }

        key.append( ")" );

        Constructor<T> constructor;

        String paramKey = key.toString();

        synchronized ( paramKey.intern() )
        {
            constructor = constructorMap.get( paramKey );

            if ( constructor == null )
            {
                @SuppressWarnings( { "unchecked" } )
                Constructor<T>[] cands = (Constructor<T>[]) targetClass.getConstructors();

                for ( Constructor<T> cand : cands )
                {
                    Class[] types = cand.getParameterTypes();

                    if ( params.length != types.length )
                    {
                        continue;
                    }

                    for ( int j = 0, len2 = params.length; j < len2; j++ )
                    {
                        if ( !types[j].isAssignableFrom( params[j] ) )
                        {
                            continue;
                        }
                    }

                    // we got it, so store it!
                    constructor = cand;
                    constructorMap.put( paramKey, constructor );
                }
            }
        }

        if ( constructor == null )
        {
            throw new ReflectorException( "Error retrieving constructor object for: " + targetClass.getName()
                + paramKey );
        }

        return constructor;
    }

    public Object getObjectProperty( Object target, String propertyName )
        throws ReflectorException
    {
        Object returnValue;

        if ( propertyName == null || propertyName.trim().length() < 1 )
        {
            throw new ReflectorException( "Cannot retrieve value for empty property." );
        }

        String beanAccessor = "get" + Character.toUpperCase( propertyName.charAt( 0 ) );
        if ( propertyName.trim().length() > 1 )
        {
            beanAccessor += propertyName.substring( 1 ).trim();
        }

        Class targetClass = target.getClass();
        Class[] emptyParams = {};

        Method method = _getMethod( targetClass, beanAccessor, emptyParams );
        if ( method == null )
        {
            method = _getMethod( targetClass, propertyName, emptyParams );
        }
        if ( method != null )
        {
            try
            {
                returnValue = method.invoke( target, new Object[] {} );
            }
            catch ( IllegalAccessException e )
            {
                throw new ReflectorException( "Error retrieving property \'" + propertyName + "\' from \'" + targetClass
                    + "\'", e );
            }
            catch ( InvocationTargetException e )
            {
                throw new ReflectorException( "Error retrieving property \'" + propertyName + "\' from \'" + targetClass
                    + "\'", e );
            }
        }

        if ( method != null )
        {
            try
            {
                returnValue = method.invoke( target, new Object[] {} );
            }
            catch ( IllegalAccessException e )
            {
                throw new ReflectorException( "Error retrieving property \'" + propertyName + "\' from \'" + targetClass
                    + "\'", e );
            }
            catch ( InvocationTargetException e )
            {
                throw new ReflectorException( "Error retrieving property \'" + propertyName + "\' from \'" + targetClass
                    + "\'", e );
            }
        }
        else
        {
            returnValue = getField( target, propertyName, true );
            if ( returnValue == null )
            {
                // TODO: Check if exception is the right action! Field exists, but contains null
                throw new ReflectorException( "Neither method: \'" + propertyName + "\' nor bean accessor: \'"
                    + beanAccessor + "\' can be found for class: \'" + targetClass + "\', and retrieval of field: \'"
                    + propertyName + "\' returned null as value." );
            }
        }

        return returnValue;
    }

    /**
     * Return the method, checking the cache first and storing in cache if not already there..
     *
     * @param targetClass The class to get the method from
     * @param params The classes of the parameters which the method should match.
     * @param methodName the method name
     * @return the Method object that matches.
     * @throws ReflectorException In case we can't retrieve the proper method.
     */
    public Method getMethod( Class targetClass, String methodName, Class[] params )
        throws ReflectorException
    {
        Method method = _getMethod( targetClass, methodName, params );

        if ( method == null )
        {
            throw new ReflectorException( "Method: \'" + methodName + "\' not found in class: \'" + targetClass
                + "\'" );
        }

        return method;
    }

    private Method _getMethod( Class targetClass, String methodName, Class[] params )
        throws ReflectorException
    {
        Map<String, Method> methodMap = (Map<String, Method>) getMethodMap( targetClass, methodName );

        StringBuilder key = new StringBuilder( 200 );

        key.append( "(" );

        for ( Class param : params )
        {
            key.append( param.getName() );
            key.append( "," );
        }

        key.append( ")" );

        Method method;

        String paramKey = key.toString();

        synchronized ( paramKey.intern() )
        {
            method = methodMap.get( paramKey );

            if ( method == null )
            {
                Method[] cands = targetClass.getMethods();

                for ( Method cand : cands )
                {
                    String name = cand.getName();

                    if ( !methodName.equals( name ) )
                    {
                        continue;
                    }

                    Class[] types = cand.getParameterTypes();

                    if ( params.length != types.length )
                    {
                        continue;
                    }

                    for ( int j = 0, len2 = params.length; j < len2; j++ )
                    {
                        if ( !types[j].isAssignableFrom( params[j] ) )
                        {
                            continue;
                        }
                    }

                    // we got it, so store it!
                    method = cand;
                    methodMap.put( paramKey, method );
                }
            }
        }

        return method;
    }

    /**
     * Retrieve the cache of constructors for the specified class.
     *
     * @param theClass the class to lookup.
     * @return The cache of constructors.
     * @throws ReflectorException in case of a lookup error.
     */
    private <T> Map<String, Constructor<T>> getConstructorMap( Class<T> theClass )
        throws ReflectorException
    {
        return (Map<String, Constructor<T>>) getMethodMap( theClass, CONSTRUCTOR_METHOD_NAME );
    }

    /**
     * Retrieve the cache of methods for the specified class and method name.
     *
     * @param theClass the class to lookup.
     * @param methodName The name of the method to lookup.
     * @return The cache of constructors.
     * @throws ReflectorException in case of a lookup error.
     */
    private Map<String, ?> getMethodMap( Class theClass, String methodName )
        throws ReflectorException
    {
        Map<String, Method> methodMap;

        if ( theClass == null )
        {
            return null;
        }

        String className = theClass.getName();

        synchronized ( className.intern() )
        {
            Map<String, Map<String, Method>> classMethods = classMaps.get( className );

            if ( classMethods == null )
            {
                classMethods = new HashMap<>();
                methodMap = new HashMap<>();
                classMethods.put( methodName, methodMap );
                classMaps.put( className, classMethods );
            }
            else
            {
                String key = className + "::" + methodName;

                synchronized ( key.intern() )
                {
                    methodMap = classMethods.get( methodName );

                    if ( methodMap == null )
                    {
                        methodMap = new HashMap<>();
                        classMethods.put( methodName, methodMap );
                    }
                }
            }
        }

        return methodMap;
    }
}
