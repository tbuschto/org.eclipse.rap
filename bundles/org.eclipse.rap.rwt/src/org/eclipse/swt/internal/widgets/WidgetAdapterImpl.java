/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.internal.lifecycle.DisposedWidgets;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetAdapter;
import org.eclipse.swt.internal.SerializableCompatibility;
import org.eclipse.swt.widgets.Widget;


public final class WidgetAdapterImpl implements WidgetAdapter, SerializableCompatibility {

  private final static Runnable[] EMPTY = new Runnable[ 0 ];

  private final String id;
  private boolean initialized;
  private transient Map<String,Object> preservedValues;
  private transient Runnable[] renderRunnables;
  private transient String cachedVariant;
  private Widget parent;

  public WidgetAdapterImpl( String id ) {
    this.id = id;
    initialize();
  }

  private void initialize() {
    preservedValues = new HashMap<String,Object>();
  }

  public String getId() {
    return id;
  }

  public void setParent( Widget parent ) {
    this.parent = parent;
  }

  public Widget getParent() {
    return parent;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public void setInitialized( boolean initialized ) {
    this.initialized = initialized;
  }

  public void preserve( String propertyName, Object value ) {
    preservedValues.put( propertyName, value );
  }

  public Object getPreserved( String propertyName ) {
    return preservedValues.get( propertyName );
  }

  public void clearPreserved() {
    preservedValues.clear();
  }

  public void addRenderRunnable( Runnable renderRunnable ) {
    if( renderRunnables == null ) {
      renderRunnables = new Runnable[] { renderRunnable };
    } else {
      Runnable[] newRunnables = new Runnable[ renderRunnables.length + 1 ];
      System.arraycopy( renderRunnables, 0, newRunnables, 0, renderRunnables.length );
      newRunnables[ newRunnables.length - 1 ] = renderRunnable;
      renderRunnables = newRunnables;
    }
  }

  public Runnable[] getRenderRunnables() {
    return renderRunnables == null ? EMPTY : renderRunnables;
  }

  public void clearRenderRunnables() {
    renderRunnables = null;
  }

  public String getCachedVariant() {
    return cachedVariant;
  }

  public void setCachedVariant( String cachedVariant ) {
    this.cachedVariant = cachedVariant;
  }

  public void markDisposed( Widget widget ) {
    if( initialized ) {
      DisposedWidgets.add( widget );
    }
  }

  private Object readResolve() {
    initialize();
    return this;
  }

}
