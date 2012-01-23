/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.VMLTest", {
  extend : qx.core.Object,

  members : {

    TARGETENGINE : [ "mshtml" ],

    testCreateCanvas : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var canvas = gfxUtil.createCanvas();
      var canvasNode = gfxUtil.getCanvasNode( canvas );
      assertEquals( "DIV", canvasNode.tagName );
      assertEquals( "0", canvasNode.style.lineHeight );
      assertEquals( "0px", canvasNode.style.fontSize );
      TestUtil.flush();
    },

    testDrawRectInWidget : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var parent = new qx.ui.basic.Terminator();
      parent.setLocation( 10, 10 );
      parent.setDimension( 400, 400 );
      parent.addToDocument();
      TestUtil.flush();
      var parentNode = parent._getTargetNode();
      var canvas = gfxUtil.createCanvas();
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.addToCanvas( canvas, shape );
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );      
      var canvasNode = parentNode.firstChild;
      assertEquals( "rect", canvasNode.firstChild.tagName );
      assertEquals( null, gfxUtil.getFillType( shape ) );
      assertTrue( gfxUtil.getDisplay( shape ) );
      parent.destroy();
      TestUtil.flush();
    },

    testFillColor : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = gfxUtil.createCanvas();
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.addToCanvas( canvas, shape );
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      gfxUtil.setFillColor( shape, null );
      assertEquals( null, gfxUtil.getFillType( shape ) );
      assertEquals( null, gfxUtil.getFillColor( shape ) );
      assertFalse( shape.fill.on );
      gfxUtil.setFillColor( shape, "green" );
      assertTrue( shape.fill.on );
      assertEquals( "solid", shape.fill.type );      
      assertEquals( "green", shape.fill.color.value );
      assertEquals( "color", gfxUtil.getFillType( shape ) );
      assertEquals( "green", gfxUtil.getFillColor( shape ) );
      parentNode.removeChild( gfxUtil.getCanvasNode( canvas ) );      
    },

    testTransparentFill : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = gfxUtil.createCanvas();
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.addToCanvas( canvas, shape );
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      gfxUtil.setFillColor( shape, "green" );
      gfxUtil.setFillColor( shape, "transparent" );
      assertEquals( null, gfxUtil.getFillType( shape ) );
      assertEquals( null, gfxUtil.getFillColor( shape ) );
      gfxUtil.setFillColor( shape, "" );
      assertEquals( null, gfxUtil.getFillType( shape ) );
      assertEquals( null, gfxUtil.getFillColor( shape ) );
      assertFalse( shape.fill.on );
      parentNode.removeChild( gfxUtil.getCanvasNode( canvas ) );      
    },

    testColorRestore : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = gfxUtil.createCanvas();
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.addToCanvas( canvas, shape );
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.setFillColor( shape, "green" );
      assertEquals( "green", gfxUtil.getFillColor( shape ) );
      parentNode.removeChild( gfxUtil.getCanvasNode( canvas ) );
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );      
      assertEquals( "green", gfxUtil.getFillColor( shape ) );
      assertFalse( "green" == shape.fill.color );
      gfxUtil.handleAppear( canvas );
      assertTrue( "green" == shape.fill.color );
      parentNode.removeChild( gfxUtil.getCanvasNode( canvas ) );      
    },

    testFillGradient : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = gfxUtil.createCanvas();
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.addToCanvas( canvas, shape );
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      gfxUtil.setFillGradient( shape, [ [ 0, "red" ], [ 1, "yellow" ] ] );
      assertTrue( shape.fill.on );
      assertEquals( "gradient", shape.fill.type );      
      assertEquals( "gradient", gfxUtil.getFillType( shape ) );
      assertEquals( "yellow", shape.fill.color2.value );
      assertEquals( 180, shape.fill.angle );
      var expected = "0 red;.25 #ff4000;.5 #ff8000;.75 #ffbf00;1 yellow";
      assertEquals( expected, shape.fill.colors.value );
      parentNode.removeChild( gfxUtil.getCanvasNode( canvas ) );      
    },

    testFillGradientHorizontal : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = gfxUtil.createCanvas();
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.addToCanvas( canvas, shape );
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      var gradient = [ [ 0, "red" ], [ 1, "yellow" ] ];
      gradient.horizontal = true;
      gfxUtil.setFillGradient( shape, gradient );
      assertTrue( shape.fill.on );
      assertEquals( "gradient", shape.fill.type );      
      assertEquals( "gradient", gfxUtil.getFillType( shape ) );
      assertEquals( "yellow", shape.fill.color2.value );
      assertEquals( 270, shape.fill.angle );
      parentNode.removeChild( gfxUtil.getCanvasNode( canvas ) );      
    },

    testDrawRoundRect : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var canvas = gfxUtil.createCanvas();
      var parent = document.body;
      shape = gfxUtil.createShape( "roundrect" );
      gfxUtil.setRoundRectLayout( shape, 10, 10, 20, 20, [ 0, 4, 3, 2 ] );
      gfxUtil.setStroke( shape, "black", 2 );
      gfxUtil.addToCanvas( canvas, shape );
      parent.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      var expected =   " m95,95 ae255,135,40,40,-17694450,-5898150 " 
                     + "ae265,265,30,30,0,-5898150 " 
                     + "ae115,275,20,20,-5898150,-5898150 x e";
      assertEquals( expected, shape.node.path.v );
      parent.removeChild( gfxUtil.getCanvasNode( canvas ) );
    },

    testDrawRoundRectOneRadiusOnlyMinimalMize : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var canvas = gfxUtil.createCanvas();
      var parent = document.body;
      shape = gfxUtil.createShape( "roundrect" );
      gfxUtil.setRoundRectLayout( shape, 10, 10, 20, 20, [ 20, 0, 0, 0] );
      gfxUtil.setStroke( shape, "black", 2 );
      gfxUtil.setFillColor( shape, "red" );
      gfxUtil.addToCanvas( canvas, shape );
      parent.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      var expected = " al195,195,100,100,-11796300,-5898150 l295,95,295,295,95,295 x e";
      assertEquals( expected, shape.node.path.v );
      parent.removeChild( gfxUtil.getCanvasNode( canvas ) );
    },

    testDrawRoundRectOneRadiusOnlyTooSmall : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var canvas = gfxUtil.createCanvas();
      var parent = document.body;
      shape = gfxUtil.createShape( "roundrect" );
      gfxUtil.setRoundRectLayout( shape, 10, 10, 20, 20, [ 21, 0, 0, 0] );
      gfxUtil.setStroke( shape, "black", 2 );
      gfxUtil.setFillColor( shape, "red" );
      gfxUtil.addToCanvas( canvas, shape );
      parent.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      var expected = " al195,195,100,100,-11796300,-5898150 l295,95,295,295,95,295 x e";
      assertEquals( expected, shape.node.path.v );
      parent.removeChild( gfxUtil.getCanvasNode( canvas ) );
    },

    testDisplay : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var parent = new qx.ui.basic.Terminator();
      parent.setLocation( 10, 10 );
      parent.setDimension( 400, 400 );
      parent.addToDocument();
      TestUtil.flush();
      var parentNode = parent._getTargetNode();
      var canvas = gfxUtil.createCanvas();
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.addToCanvas( canvas, shape );
      gfxUtil.setDisplay( shape, false );
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );      
      var canvasNode = parentNode.firstChild;
      assertFalse( shape.fill.on );
      assertFalse( gfxUtil.getDisplay( shape ) );
      parent.destroy();
      TestUtil.flush();
    },
    
    testFillPattern : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var parentNode = document.body
      var canvas = gfxUtil.createCanvas();
      shape = gfxUtil.createShape( "rect" );
      gfxUtil.setRectBounds( shape, 10, 10, 100, 100 );
      gfxUtil.setStroke( shape, "black", 2 );
      gfxUtil.setFillPattern( shape, "./js/resource/tex.jpg", 70, 70 );
      assertTrue( shape.fill.on );
      gfxUtil.addToCanvas( canvas, shape );
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      assertEquals( "rect", shape.node.tagName );
      assertTrue( shape.fill.on );      
      assertEquals( "tile", shape.fill.type );      
      assertEquals( "./js/resource/tex.jpg", shape.fill.src );
      assertEquals( "pattern", gfxUtil.getFillType( shape ) );
      parentNode.removeChild( gfxUtil.getCanvasNode( canvas ) );
    },
    
    testOpacity : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = gfxUtil.createCanvas();
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.addToCanvas( canvas, shape );
      gfxUtil.setOpacity( shape, 0.5 );
      assertTrue( shape.node.style.cssText.indexOf( "FILTER:" ) != -1 );
      assertTrue( shape.node.style.filter.indexOf( "opacity=50" ) != -1 );
      assertEquals( 0.5, gfxUtil.getOpacity( shape ) ); 
      gfxUtil.setOpacity( shape, 1 );
      // It is important for some issues that filter is completely removed:
      assertTrue( shape.node.style.cssText.indexOf( "FILTER:" ) == -1 );
      assertEquals( 1, gfxUtil.getOpacity( shape ) ); 
      parentNode.removeChild( gfxUtil.getCanvasNode( canvas ) );      
    },

    testBlur : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = gfxUtil.createCanvas();
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.addToCanvas( canvas, shape );
      gfxUtil.setBlur( shape, 4 );
      assertTrue( shape.node.style.cssText.indexOf( "FILTER:" ) != -1 );
      var filter = shape.node.style.filter;
      var expected = "progid:DXImageTransform.Microsoft.Blur(pixelradius=4)";
      assertTrue( filter.indexOf( expected ) != -1 );
      assertEquals( 4, gfxUtil.getBlur( shape ) ); 
      gfxUtil.setBlur( shape, 0 );
      // It is important for some issues that filter is completely removed:
      assertTrue( shape.node.style.cssText.indexOf( "FILTER:" ) == -1 );
      assertEquals( 0, gfxUtil.getBlur( shape ) ); 
      parentNode.removeChild( gfxUtil.getCanvasNode( canvas ) );
    },

    testBlurWithOpacity : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = gfxUtil.createCanvas();
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.addToCanvas( canvas, shape );
      gfxUtil.setBlur( shape, 4 );
      gfxUtil.setOpacity( shape, 0.5 );
      assertTrue( shape.node.style.cssText.indexOf( "FILTER:" ) != -1 );
      var alpha = "Alpha(opacity=50)";
      var blur = "progid:DXImageTransform.Microsoft.Blur(pixelradius=4)";
      assertEquals( alpha+blur, shape.node.style.filter );
      gfxUtil.setBlur( shape, 0 );
      assertEquals( alpha, shape.node.style.filter );
      gfxUtil.setBlur( shape, 4 );
      assertEquals( alpha+blur, shape.node.style.filter );
      gfxUtil.setOpacity( shape, 1 );
      assertEquals( blur, shape.node.style.filter );
      gfxUtil.setBlur( shape, 0 );
      assertTrue( shape.node.style.cssText.indexOf( "FILTER:" ) == -1 );
      parentNode.removeChild( gfxUtil.getCanvasNode( canvas ) );
    },
    
    testNodeOrder : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var parent = document.body;
      canvas = gfxUtil.createCanvas();
      parent.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      var shape1 = gfxUtil.createShape( "rect" );
      var shape2 = gfxUtil.createShape( "rect" );
      var shape3 = gfxUtil.createShape( "rect" );
      gfxUtil.addToCanvas( canvas, shape1 );
      gfxUtil.addToCanvas( canvas, shape3 );
      gfxUtil.addToCanvas( canvas, shape2, shape3 );
      var nodes = canvas.node.childNodes;
      assertIdentical( nodes[ 0 ], shape1.node );
      assertIdentical( nodes[ 1 ], shape2.node );
      assertIdentical( nodes[ 2 ], shape3.node );
      TestUtil.flush();
    }


  }
  
} );