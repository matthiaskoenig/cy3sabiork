package org.cy3sabiork.oven;

import java.applet.Applet;

// FIXME: need URL on oracle.com for new LiveConnect spec

/**
 * <P> Allows Java code to manipulate JavaScript objects. </P>
 *
 * <P> When a JavaScript object is passed or returned to Java code, it
 * is wrapped in an instance of <CODE>JSObject</CODE>. When a
 * <CODE>JSObject</CODE> instance is passed to the JavaScript engine,
 * it is unwrapped back to its original JavaScript object. The
 * <CODE>JSObject</CODE> class provides a way to invoke JavaScript
 * methods and examine JavaScript properties. </P>
 *
 * <P> Any data returned from the JavaScript engine to Java is
 * converted to Java data types. Certain data passed to the JavaScript
 * engine is converted to JavaScript data types. See the section on <A
 * HREF="https://jdk6.dev.java.net/plugin2/liveconnect/#JAVA_JS_CONVERSIONS">Data
 * Type Conversions</A> in the <A
 * HREF="https://jdk6.dev.java.net/plugin2/liveconnect/">new
 * LiveConnect Specification</A> for details on how values are
 * converted. </P>
 *
 */
public abstract class JSObject {

    /**
     * Constructs a new JSObject. Users should not call this method
     * nor subclass JSObject.
     */
    protected JSObject()  {
    }

    /**
     * <p> Calls a JavaScript method. Equivalent to
     * "this.methodName(args[0], args[1], ...)" in JavaScript.
     * </p>
     *
     * @param methodName The name of the JavaScript method to be invoked.
     * @param args the Java objects passed as arguments to the method.
     * @return Result of the method.
     */
    public abstract Object call(String methodName, Object... args) throws JSException;

    /**
     * <p> Evaluates a JavaScript expression. The expression is a string of
     * JavaScript source code which will be evaluated in the context given by
     * "this".
     * </p>
     *
     * @param s The JavaScript expression.
     * @return Result of the JavaScript evaluation.
     */
    public abstract Object eval(String s) throws JSException;

    /**
     * <p> Retrieves a named member of a JavaScript object. Equivalent to
     * "this.name" in JavaScript.
     * </p>
     *
     * @param name The name of the JavaScript property to be accessed.
     * @return The value of the propery.
     */
    public abstract Object getMember(String name) throws JSException;

    /**
     * <p> Sets a named member of a JavaScript object. Equivalent to
     * "this.name = value" in JavaScript.
     * </p>
     *
     * @param name The name of the JavaScript property to be accessed.
     * @param value The value of the propery.
     */
    public abstract void setMember(String name, Object value) throws JSException;

    /**
     * <p> Removes a named member of a JavaScript object. Equivalent
     * to "delete this.name" in JavaScript.
     * </p>
     *
     * @param name The name of the JavaScript property to be removed.
     */
    public abstract void removeMember(String name) throws JSException;

    /**
     * <p> Retrieves an indexed member of a JavaScript object. Equivalent to
     * "this[index]" in JavaScript.
     * </p>
     *
     * @param index The index of the array to be accessed.
     * @return The value of the indexed member.
     */
    public abstract Object getSlot(int index) throws JSException;

    /**
     * <p> Sets an indexed member of a JavaScript object. Equivalent to
     * "this[index] = value" in JavaScript.
     * </p>
     *
     * @param index The index of the array to be accessed.
     */
    public abstract void setSlot(int index, Object value) throws JSException;

    /**
     * <p> Returns a JSObject for the window containing the given applet.
     * </p>
     *
     * @param applet The applet.
     * @return JSObject for the window containing the given applet.
     */
    public static JSObject getWindow(Applet applet) throws JSException {
        throw new JSException("Unexpected error: This method should not be used unless loaded from plugin.jar");

    }
}