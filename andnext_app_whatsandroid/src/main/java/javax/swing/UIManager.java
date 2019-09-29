/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package javax.swing;

import java.io.Serializable;


/**
 * {@code UIManager} manages the current look and feel, the set of
 * available look and feels, {@code PropertyChangeListeners} that
 * are notified when the look and feel changes, look and feel defaults, and
 * convenience methods for obtaining various default values.
 *
 * <h3>Specifying the look and feel</h3>
 *
 * The look and feel can be specified in two distinct ways: by
 * specifying the fully qualified name of the class for the look and
 * feel, or by creating an instance of {@code LookAndFeel} and passing
 * it to {@code setLookAndFeel}. The following example illustrates
 * setting the look and feel to the system look and feel:
 * <pre>
 *   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
 * </pre>
 * The following example illustrates setting the look and feel based on
 * class name:
 * <pre>
 *   UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
 * </pre>
 * Once the look and feel has been changed it is imperative to invoke
 * {@code updateUI} on all {@code JComponents}. The method {@link
 * SwingUtilities#updateComponentTreeUI} makes it easy to apply {@code
 * updateUI} to a containment hierarchy. Refer to it for
 * details. The exact behavior of not invoking {@code
 * updateUI} after changing the look and feel is
 * unspecified. It is very possible to receive unexpected exceptions,
 * painting problems, or worse.
 *
 * <h3>Default look and feel</h3>
 *
 * The class used for the default look and feel is chosen in the following
 * manner:
 * <ol>
 *   <li>If the system property <code>swing.defaultlaf</code> is
 *       {@code non-null}, use its value as the default look and feel class
 *       name.
 *   <li>If the {@link java.util.Properties} file <code>swing.properties</code>
 *       exists and contains the key <code>swing.defaultlaf</code>,
 *       use its value as the default look and feel class name. The location
 *       that is checked for <code>swing.properties</code> may vary depending
 *       upon the implementation of the Java platform. Typically the
 *       <code>swing.properties</code> file is located in the <code>lib</code>
 *       subdirectory of the Java installation directory.
 *       Refer to the release notes of the implementation being used for
 *       further details.
 *   <li>Otherwise use the cross platform look and feel.
 * </ol>
 *
 * <h3>Defaults</h3>
 *
 * {@code UIManager} manages three sets of {@code UIDefaults}. In order, they
 * are:
 * <ol>
 *   <li>Developer defaults. With few exceptions Swing does not
 *       alter the developer defaults; these are intended to be modified
 *       and used by the developer.
 *   <li>Look and feel defaults. The look and feel defaults are
 *       supplied by the look and feel at the time it is installed as the
 *       current look and feel ({@code setLookAndFeel()} is invoked). The
 *       look and feel defaults can be obtained using the {@code
 *       getLookAndFeelDefaults()} method.
 *   <li>System defaults. The system defaults are provided by Swing.
 * </ol>
 * Invoking any of the various {@code get} methods
 * results in checking each of the defaults, in order, returning
 * the first {@code non-null} value. For example, invoking
 * {@code UIManager.getString("Table.foreground")} results in first
 * checking developer defaults. If the developer defaults contain
 * a value for {@code "Table.foreground"} it is returned, otherwise
 * the look and feel defaults are checked, followed by the system defaults.
 * <p>
 * It's important to note that {@code getDefaults} returns a custom
 * instance of {@code UIDefaults} with this resolution logic built into it.
 * For example, {@code UIManager.getDefaults().getString("Table.foreground")}
 * is equivalent to {@code UIManager.getString("Table.foreground")}. Both
 * resolve using the algorithm just described. In many places the
 * documentation uses the word defaults to refer to the custom instance
 * of {@code UIDefaults} with the resolution logic as previously described.
 * <p>
 * When the look and feel is changed, {@code UIManager} alters only the
 * look and feel defaults; the developer and system defaults are not
 * altered by the {@code UIManager} in any way.
 * <p>
 * The set of defaults a particular look and feel supports is defined
 * and documented by that look and feel. In addition, each look and
 * feel, or {@code ComponentUI} provided by a look and feel, may
 * access the defaults at different times in their life cycle. Some
 * look and feels may aggressively look up defaults, so that changing a
 * default may not have an effect after installing the look and feel.
 * Other look and feels may lazily access defaults so that a change to
 * the defaults may effect an existing look and feel. Finally, other look
 * and feels might not configure themselves from the defaults table in
 * any way. None-the-less it is usually the case that a look and feel
 * expects certain defaults, so that in general
 * a {@code ComponentUI} provided by one look and feel will not
 * work with another look and feel.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans&trade;
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @author Thomas Ball
 * @author Hans Muller
 */
public class UIManager implements Serializable
{
    /**
     * Returns a string from the defaults. If the value for
     * {@code key} is not a {@code String}, {@code null} is returned.
     *
     * @param key  an <code>Object</code> specifying the string
     * @return the <code>String</code>
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public static String getString(Object key) {
        if (key != null) {
            return key.toString();
        }

        return "";
    }

}
