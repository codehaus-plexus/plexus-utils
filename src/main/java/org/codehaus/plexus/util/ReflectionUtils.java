package org.codehaus.plexus.util;

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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.AccessibleObject;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Operations on a class' fields and their setters.
 * 
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @author <a href="mailto:jesse@codehaus.org">Jesse McConnell</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 */
public final class ReflectionUtils
{
    // ----------------------------------------------------------------------
    // Field utils
    // ----------------------------------------------------------------------

    public static Field getFieldByNameIncludingSuperclasses( String fieldName, Class<?> clazz )
    {
        Field retValue = null;

        try
        {
            retValue = clazz.getDeclaredField( fieldName );
        }
        catch ( NoSuchFieldException e )
        {
            Class<?> superclass = clazz.getSuperclass();

            if ( superclass != null )
            {
                retValue = getFieldByNameIncludingSuperclasses( fieldName, superclass );
            }
        }

        return retValue;
    }

    public static List<Field> getFieldsIncludingSuperclasses( Class<?> clazz )
    {
        List<Field> fields = new ArrayList<>( Arrays.asList( clazz.getDeclaredFields() ) );

        Class<?> superclass = clazz.getSuperclass();

        if ( superclass != null )
        {
            fields.addAll( getFieldsIncludingSuperclasses( superclass ) );
        }

        return fields;
    }

    // ----------------------------------------------------------------------
    // Setter utils
    // ----------------------------------------------------------------------

    /**
     * Finds a setter in the given class for the given field. It searches interfaces and superclasses too.
     *
     * @param fieldName the name of the field (i.e. 'fooBar'); it will search for a method named 'setFooBar'.
     * @param clazz The class to find the method in.
     * @return null or the method found.
     */
    public static Method getSetter( String fieldName, Class<?> clazz )
    {
        Method[] methods = clazz.getMethods();

        fieldName = "set" + StringUtils.capitalizeFirstLetter( fieldName );

        for ( Method method : methods )
        {
            if ( method.getName().equals( fieldName ) && isSetter( method ) )
            {
                return method;
            }
        }

        return null;
    }

    /**
     * @return all setters in the given class and super classes.
     * @param clazz the Class
     */
    public static List<Method> getSetters( Class<?> clazz )
    {
        Method[] methods = clazz.getMethods();

        List<Method> list = new ArrayList<>();

        for ( Method method : methods )
        {
            if ( isSetter( method ) )
            {
                list.add( method );
            }
        }

        return list;
    }

    /**
     * @param method the method
     * @return the class of the argument to the setter. Will throw an RuntimeException if the method isn't a setter.
     */
    public static Class<?> getSetterType( Method method )
    {
        if ( !isSetter( method ) )
        {
            throw new RuntimeException( "The method " + method.getDeclaringClass().getName() + "." + method.getName()
                + " is not a setter." );
        }

        return method.getParameterTypes()[0];
    }

    // ----------------------------------------------------------------------
    // Value accesstors
    // ----------------------------------------------------------------------

    /**
     * attempts to set the value to the variable in the object passed in
     *
     * @param object see name
     * @param variable see name
     * @param value see name
     * @throws IllegalAccessException if error
     */
    public static void setVariableValueInObject( Object object, String variable, Object value )
        throws IllegalAccessException
    {
        Field field = getFieldByNameIncludingSuperclasses( variable, object.getClass() );

        field.setAccessible( true );

        field.set( object, value );
    }

    /**
     * Generates a map of the fields and values on a given object, also pulls from superclasses
     * 
     * @param variable field name
     * @param object the object to generate the list of fields from
     * @return map containing the fields and their values
     * @throws IllegalAccessException cannot access
     */
    public static Object getValueIncludingSuperclasses( String variable, Object object )
        throws IllegalAccessException
    {

        Field field = getFieldByNameIncludingSuperclasses( variable, object.getClass() );

        field.setAccessible( true );

        return field.get( object );
    }

    /**
     * Generates a map of the fields and values on a given object, also pulls from superclasses
     *
     * @param object the object to generate the list of fields from
     * @return map containing the fields and their values
     * @throws IllegalAccessException cannot access
     */
    public static Map<String, Object> getVariablesAndValuesIncludingSuperclasses( Object object )
        throws IllegalAccessException
    {
        Map<String, Object> map = new HashMap<>();

        gatherVariablesAndValuesIncludingSuperclasses( object, map );

        return map;
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    public static boolean isSetter( Method method )
    {
        return method.getReturnType().equals( Void.TYPE ) && // FIXME: needed /required?
            !Modifier.isStatic( method.getModifiers() ) && method.getParameterTypes().length == 1;
    }

    /**
     * populates a map of the fields and values on a given object, also pulls from superclasses
     *
     * @param object the object to generate the list of fields from
     * @param map to populate
     */
    private static void gatherVariablesAndValuesIncludingSuperclasses( Object object, Map<String, Object> map )
        throws IllegalAccessException
    {

        Class<?> clazz = object.getClass();

        if ( Float.parseFloat( System.getProperty( "java.specification.version" ) ) >= 11
            && Class.class.getCanonicalName().equals( clazz.getCanonicalName() ) )
        {
            // Updating Class fields accessibility is forbidden on Java 16 (and throws warning from version 11)
            // No concrete use case to modify accessibility at this level
            return;
        }

        Field[] fields = clazz.getDeclaredFields();

        AccessibleObject.setAccessible( fields, true );

        for ( Field field : fields )
        {
            map.put( field.getName(), field.get( object ) );

        }

        Class<?> superclass = clazz.getSuperclass();

        if ( !Object.class.equals( superclass ) )
        {
            gatherVariablesAndValuesIncludingSuperclasses( superclass, map );
        }
    }
}
