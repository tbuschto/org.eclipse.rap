/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

org.eclipse.rwt.protocol.AdapterRegistry.add( "org.eclipse.swt.widgets.Label", {

  factory : function( properties ) {
    var result = new qx.ui.basic.Atom();
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( properties.parent, function( parent ) {
      result.setParent( parent );
    } );
    org.eclipse.swt.LabelUtil.initialize( result );
    org.eclipse.swt.LabelUtil.setWrap( result, properties.style.indexOf( "WRAP" ) != -1 );    
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {} ),

  knownListeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  knownMethods : [
    "allowEvent",
    "cancelEvent"
  ],

  methodHandler : {
    "allowEvent" : function( widget, properties ) {
      org.eclipse.rwt.KeyEventUtil.getInstance().allowEvent();
    },
    "cancelEvent" : function( widget, properties ) {
      org.eclipse.rwt.KeyEventUtil.getInstance().cancelEvent();
    }
  }

} );