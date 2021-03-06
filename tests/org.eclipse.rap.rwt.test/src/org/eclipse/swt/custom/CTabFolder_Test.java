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
package org.eclipse.swt.custom;

import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.custom.ICTabFolderAdapter;
import org.eclipse.swt.internal.events.EventTypes;
import org.eclipse.swt.internal.widgets.IWidgetGraphicsAdapter;
import org.eclipse.swt.internal.widgets.ItemHolder;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CTabFolder_Test {

  private Shell shell;
  private Display display;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testInitialValues() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    assertFalse( folder.getMRUVisible() );
    assertFalse( folder.getMaximizeVisible() );
    assertFalse( folder.getMinimizeVisible() );
    assertFalse( folder.getMaximized() );
    assertFalse( folder.getMinimized() );
    assertFalse( folder.getSingle() );
    assertEquals( SWT.TOP, folder.getTabPosition() );
    assertEquals( null, folder.getToolTipText() );
    assertEquals( 20, folder.getMinimumCharacters() );
    assertFalse( folder.getBorderVisible() );
    assertNotNull( folder.getSelectionBackground() );
  }

  @Test
  public void testHierarchy() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    assertEquals( 0, folder.getItemCount() );
    assertTrue( Arrays.equals( new CTabItem[ 0 ], folder.getItems() ) );

    CTabItem item = new CTabItem( folder, SWT.NONE );
    assertTrue( Composite.class.isAssignableFrom( folder.getClass() ) );
    assertSame( folder, item.getParent() );
    assertSame( folder.getDisplay(), item.getDisplay() );
    assertEquals( 1, folder.getItemCount() );
    assertSame( item, folder.getItem( 0 ) );
    assertSame( item, folder.getItems()[ 0 ] );
    assertEquals( 0, folder.indexOf( item ) );
    Control control = new Label( folder, SWT.NONE );
    item.setControl( control );
    assertSame( control, item.getControl() );
    try {
      item.setControl( shell );
      fail( "Wrong parent." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
  }

  @Test
  public void testDispose() {
    final StringBuilder  log = new StringBuilder();
    CTabFolder folder1 = new CTabFolder( shell, SWT.NONE );
    folder1.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        log.append( "selectionEvent" );
      }
    } );
    CTabItem item1 = new CTabItem( folder1, SWT.NONE );
    CTabItem item2 = new CTabItem( folder1, SWT.NONE );
    folder1.setSelection( item2 );
    CTabItem item3 = new CTabItem( folder1, SWT.NONE );

    item3.dispose();
    assertTrue( item3.isDisposed() );
    assertEquals( 2, folder1.getItemCount() );
    assertEquals( -1, folder1.indexOf( item3 ) );

    folder1.dispose();
    assertTrue( folder1.isDisposed() );
    assertTrue( item1.isDisposed() );
    assertEquals( 0, ItemHolder.getItemHolder( folder1 ).getItems().length );

    // Ensure that no SelectionEvent is sent when disposing of a CTabFolder
    assertEquals( "", log.toString() );
  }

  @Test
  public void testStyle() {
    CTabFolder folder1 = new CTabFolder( shell, SWT.NONE );
    assertEquals( SWT.TOP | SWT.MULTI | SWT.LEFT_TO_RIGHT, folder1.getStyle() );
    assertEquals( SWT.TOP, folder1.getTabPosition() );
    assertFalse( folder1.getSingle() );

    CTabFolder folder2 = new CTabFolder( shell, -1 );
    assertTrue( ( folder2.getStyle() & SWT.MULTI ) != 0 );
    assertTrue( ( folder2.getStyle() & SWT.TOP ) != 0 );

    int styles = SWT.TOP | SWT.BOTTOM | SWT.SINGLE | SWT.MULTI;
    CTabFolder folder3 = new CTabFolder( shell, styles );
    assertTrue( ( folder3.getStyle() & SWT.MULTI ) != 0 );
    assertTrue( ( folder3.getStyle() & SWT.TOP ) != 0 );

    styles = SWT.BOTTOM | SWT.SINGLE;
    CTabFolder folder4 = new CTabFolder( shell, styles );
    assertTrue( ( folder4.getStyle() & SWT.SINGLE ) != 0 );
    assertTrue( ( folder4.getStyle() & SWT.BOTTOM ) != 0 );
    assertEquals( SWT.BOTTOM, folder4.getTabPosition() );
    assertTrue( folder4.getSingle() );

    CTabFolder folder5 = new CTabFolder( shell, SWT.BORDER );
    assertTrue( folder5.getBorderVisible() );

    CTabFolder folder6 = new CTabFolder( shell, SWT.NONE );
    assertFalse( folder6.getBorderVisible() );
  }

  @Test
  public void testSelectionIndex() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    // Test folder without items: initial value must be -1 / null
    assertEquals( -1, folder.getSelectionIndex() );
    assertEquals( null, folder.getSelection() );
    // Setting a selection index out of range must simply be ignored
    folder.setSelection( 2 );
    assertEquals( -1, folder.getSelectionIndex() );
    assertEquals( null, folder.getSelection() );
    folder.setSelection( -2 );
    assertEquals( -1, folder.getSelectionIndex() );
    assertEquals( null, folder.getSelection() );
    folder.setSelection( 0 );
    assertEquals( -1, folder.getSelectionIndex() );
    assertEquals( null, folder.getSelection() );

    // Add an item -> must not change selection index
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    assertEquals( -1, folder.getSelectionIndex() );
    assertEquals( null, folder.getSelection() );

    folder.setSelection( 0 );
    assertEquals( 0, folder.getSelectionIndex() );
    assertSame( item1, folder.getSelection() );

    // Test that there is no way to unset a selection
    folder.setSelection( 0 );
    folder.setSelection( -1 );
    assertEquals( 0, folder.getSelectionIndex() );
    assertSame( item1, folder.getSelection() );

    CTabItem item2 = new CTabItem( folder, SWT.NONE );
    folder.setSelection( item2 );
    assertEquals( 1, folder.getSelectionIndex() );
    assertSame( item2, folder.getSelection() );

    item1.dispose();
    assertSame( item2, folder.getSelection() );
    assertEquals( 0, folder.getSelectionIndex() );

    item2.dispose();
    assertEquals( null, folder.getSelection() );
  }

  @Test
  public void testSelectionWithControl() {
    CTabFolder folder = new CTabFolder( shell, SWT.MULTI );
    folder.setSize( 100, 200 );
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    Control control1 = new Label( folder, SWT.NONE );
    item1.setControl( control1 );
    CTabItem item2 = new CTabItem( folder, SWT.NONE );
    Control control2 = new Label( folder, SWT.NONE );
    item2.setControl( control2 );
    CTabItem item3 = new CTabItem( folder, SWT.NONE );
    shell.open();

    folder.setSelection( item1 );
    assertTrue( item1.getControl().getVisible() );
    assertEquals( folder.getClientArea(), item1.getControl().getBounds() );

    folder.setSelection( item2 );
    assertFalse( item1.getControl().getVisible() );
    assertTrue( item2.getControl().getVisible() );
    assertEquals( folder.getClientArea(), item2.getControl().getBounds() );

    folder.setSelection( item3 );
    assertFalse( item2.getControl().getVisible() );
  }

  @Test
  public void testSelectionWithEvent() {
    final StringBuilder log = new StringBuilder();
    final CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    final CTabItem item1 = new CTabItem( folder, SWT.NONE );
    CTabItem item2 = new CTabItem( folder, SWT.NONE );
    folder.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        assertSame( folder, event.getSource() );
        assertSame( item1, event.item );
        assertEquals( 0, event.detail );
        assertTrue( event.doit );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        log.append( "widgetSelected" );
      }
    } );
    folder.setSelection( item2 );
    item2.dispose();
    assertEquals( "widgetSelected", log.toString() );
  }

  @Test
  public void testMinimizeMaximize() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    // Test initial state
    assertFalse( folder.getMinimized() );
    assertFalse( folder.getMaximized() );

    // set minimized to the same value it has -> nothing should change
    folder.setMinimized( false );
    assertFalse( folder.getMinimized() );
    assertFalse( folder.getMaximized() );

    // minimize
    folder.setMinimized( true );
    assertTrue( folder.getMinimized() );
    assertFalse( folder.getMaximized() );

    // set maximize to the current value -> nothing should happen
    folder.setMaximized( false );
    assertTrue( folder.getMinimized() );
    assertFalse( folder.getMaximized() );

    // maximize
    folder.setMaximized( true );
    assertFalse( folder.getMinimized() );
    assertTrue( folder.getMaximized() );
  }

  @Test
  public void testMinMaxVisible() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    // test getter/setter
    folder.setMinimizeVisible( false );
    assertFalse( folder.getMinimizeVisible() );
    folder.setMaximizeVisible( false );
    assertFalse( folder.getMinimizeVisible() );
  }

  @Test
  public void testResize() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    folder.setMinimizeVisible( true );
    folder.setMaximizeVisible( true );
    new CTabItem( folder, SWT.NONE );
    // set initial size and store position of min/max button
    folder.setSize( 200, 200 );
    shell.layout();
    Object adapter = folder.getAdapter( ICTabFolderAdapter.class );
    ICTabFolderAdapter folderAdapter = ( ICTabFolderAdapter )adapter;
    Rectangle oldMinBounds = folderAdapter.getMinimizeRect();
    Rectangle oldMaxBounds = folderAdapter.getMaximizeRect();
    // resize folder: must move min/max buttons
    folder.setSize( 150, folder.getSize().y );
    Rectangle newMinBounds = folderAdapter.getMinimizeRect();
    Rectangle newMaxBounds = folderAdapter.getMaximizeRect();
    assertTrue( newMinBounds.x < oldMinBounds.x );
    assertTrue( newMaxBounds.x < oldMaxBounds.x );
  }

  @Test
  public void testLayout() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    assertEquals( CTabFolderLayout.class, folder.getLayout().getClass() );

    folder.setLayout( new FillLayout() );
    assertEquals( CTabFolderLayout.class, folder.getLayout().getClass() );
  }

  @Test
  public void testTabHeight() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    // Test initial value
    assertTrue( folder.getTabHeight() > 0 );
    folder.setTabHeight( 30 );
    assertEquals( 30, folder.getTabHeight() );
    folder.setTabHeight( SWT.DEFAULT );
    assertTrue( folder.getTabHeight() > 0 );
    try {
      folder.setTabHeight( -2 );
      fail( "tabHeight must be DEFAULT or positive value" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=279592
  @Test
  public void testTabHeightImage() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    CTabItem item = new CTabItem( folder, SWT.CLOSE );
    item.setText( "foo" );
    int textOnlyHeight = folder.getTabHeight();
    assertTrue( textOnlyHeight > 0 );
    item.setImage( display.getSystemImage( SWT.ICON_ERROR ) );
    assertTrue( folder.getTabHeight() > textOnlyHeight );
  }

  @Test
  public void testTopRight() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    ToolBar toolBar = new ToolBar( folder, SWT.NONE );
    // Test initial value
    assertEquals( null, folder.getTopRight() );
    // set toolbar
    folder.setTopRight( toolBar );
    assertSame( toolBar, folder.getTopRight() );
    folder.setTopRight( toolBar, SWT.FILL );
    assertSame( toolBar, folder.getTopRight() );
    folder.setTopRight( null );
    assertEquals( null, folder.getTopRight() );
    // Test illegal values
    try {
      folder.setTopRight( shell );
      fail( "setTopRight must check for invalid parent" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      folder.setTopRight( toolBar, SWT.LEFT );
      fail( "setTopRight must check for legal alignment values" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // Set invisible topRight control
    ToolBar invisibleToolBar = new ToolBar( folder, SWT.NONE );
    invisibleToolBar.setVisible( false );
    folder.setTopRight( invisibleToolBar );
    assertFalse( invisibleToolBar.isVisible() );
  }

  @Test
  public void testSelectionForegroundAndBackground() {
    CTabFolder folder = new CTabFolder( shell, SWT.MULTI );

    // Set some background color
    Color red = display.getSystemColor( SWT.COLOR_RED );
    folder.setSelectionBackground( red );
    assertEquals( red, folder.getSelectionBackground() );

    // Reset to background to default (pass null as parameter)
    folder.setSelectionBackground( ( Color )null );
    assertNotNull( folder.getSelectionBackground() );

    // Set some foreground color
    Color white = display.getSystemColor( SWT.COLOR_WHITE );
    folder.setSelectionForeground( white );
    assertEquals( white, folder.getSelectionForeground() );

    // Reset to foreground to default (pass null as parameter)
    folder.setSelectionForeground( null );
    assertNotNull( folder.getSelectionForeground() );
  }

  @Test
  public void testSelectionBackgroundGradient() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Object adapter = folder.getAdapter( ICTabFolderAdapter.class );
    ICTabFolderAdapter folderAdapter = ( ICTabFolderAdapter )adapter;
    new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );

    Color[] colors = new Color[] {
      display.getSystemColor( SWT.COLOR_RED ),
      display.getSystemColor( SWT.COLOR_WHITE ),
      display.getSystemColor( SWT.COLOR_RED )
    };
    int[] percents = new int[] { 50, 100 };
    folder.setSelectionBackground( display.getSystemColor( SWT.COLOR_BLUE ) );
    assertEquals( display.getSystemColor( SWT.COLOR_BLUE ),
                  folderAdapter.getUserSelectionBackground() );
    folder.setSelectionBackground( colors, percents );
    IWidgetGraphicsAdapter gfxAdapter
      = folderAdapter.getUserSelectionBackgroundGradient();
    Color[] gfxColors = gfxAdapter.getBackgroundGradientColors();
    assertEquals( colors[ 0 ], gfxColors[ 0 ] );
    assertEquals( colors[ 1 ], gfxColors[ 1 ] );
    assertEquals( colors[ 2 ], gfxColors[ 2 ] );
    assertEquals( colors[ 2 ], folder.getSelectionBackground() );
    int[] gfxPercents = gfxAdapter.getBackgroundGradientPercents();
    assertEquals( 0, gfxPercents[ 0 ] );
    assertEquals( percents[ 0 ], gfxPercents[ 1 ] );
    assertEquals( percents[ 1 ], gfxPercents[ 2 ] );

    folder.setSelectionBackground( null, null );
    // resetting background gradient also resets background color
    assertNull( folderAdapter.getUserSelectionBackground() );
    gfxAdapter = folderAdapter.getUserSelectionBackgroundGradient();
    gfxColors = gfxAdapter.getBackgroundGradientColors();
    gfxPercents = gfxAdapter.getBackgroundGradientPercents();
    assertNull( gfxColors );
    assertNull( gfxPercents );

    percents = new int[] { 0, 50, 100 };
    try {
      folder.setSelectionBackground( colors, percents );
      fail( "Wrong gradient arrays length." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
    percents = new int[] { -50, 100 };
    try {
      folder.setSelectionBackground( colors, percents );
      fail( "Wrong gradient percents value." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
    percents = new int[] { 100, 50 };
    try {
      folder.setSelectionBackground( colors, percents );
      fail( "Wrong gradient percents value." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
    percents = new int[] { 50, 150 };
    try {
      folder.setSelectionBackground( colors, percents );
      fail( "Wrong gradient percents value." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
    colors[ 1 ] = null;
    try {
      folder.setSelectionBackground( colors, percents );
      fail( "Wrong gradient colors value." );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
  }

  // bug 329013
  @Test
  public void testSelectionBackgroundGradientWithHighlightColor() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Object adapter = folder.getAdapter( ICTabFolderAdapter.class );
    ICTabFolderAdapter folderAdapter = ( ICTabFolderAdapter )adapter;
    new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );

    Color[] colors = new Color[] {
      display.getSystemColor( SWT.COLOR_RED ),
      display.getSystemColor( SWT.COLOR_WHITE ),
      display.getSystemColor( SWT.COLOR_RED )
    };
    int[] percents = new int[] { 0 };
    try {
      folder.setSelectionBackground( colors, percents );
    } catch( IllegalArgumentException iae ) {
      fail( "Should not throw IAE when percents.lenght == colors.lenght - 2." );
    }
    IWidgetGraphicsAdapter gfxAdapter
      = folderAdapter.getUserSelectionBackgroundGradient();
    Color[] gfxColors = gfxAdapter.getBackgroundGradientColors();
    assertEquals( 2, gfxColors.length );
    assertEquals( colors[ 0 ], gfxColors[ 0 ] );
    assertEquals( colors[ 1 ], gfxColors[ 1 ] );
    assertEquals( colors[ 1 ], folder.getSelectionBackground() );
    int[] gfxPercents = gfxAdapter.getBackgroundGradientPercents();
    assertEquals( 2, gfxPercents.length );
    assertEquals( 0, gfxPercents[ 0 ] );
    assertEquals( 0, gfxPercents[ 1 ] );
  }

  @Test
  public void testSelectionBackgroundImage() throws IOException {
    CTabFolder folder = new CTabFolder( shell, SWT.MULTI );
    Object adapter = folder.getAdapter( ICTabFolderAdapter.class );
    ICTabFolderAdapter folderAdapter = ( ICTabFolderAdapter )adapter;
    assertNull( folderAdapter.getUserSelectionBackgroundImage() );
    Image image = createImage( display, Fixture.IMAGE1 );
    folder.setSelectionBackground( image );
    assertEquals( image, folderAdapter.getUserSelectionBackgroundImage() );
    folder.setSelectionBackground( ( Image )null );
    assertNull( folderAdapter.getUserSelectionBackgroundImage() );
  }

  @Test
  public void testChevronVisibilityWithSingleStyle() {
    shell.setSize( 150, 150 );
    CTabFolder folder = new CTabFolder( shell, SWT.SINGLE | SWT.CLOSE | SWT.BORDER );
    folder.setSize( 100, 100 );
    folder.addCTabFolder2Listener( new CTabFolder2Adapter() );
    shell.layout();

    // Chevron must be visible when there are no items
    assertEquals( 0, folder.getItemCount() );  // ensure test condition
    assertTrue( getChevronVisible( folder ) );

    // Behave as SWT does even if it may be a bug
    // Chevron is visible but its bounds are zero if there is only *one* item
    // wich is *selected*
    CTabItem item = new CTabItem( folder, SWT.NONE );
    item.setText( "item" );
    Label label = new Label( folder, SWT.NONE );
    item.setControl( label );
    folder.setSelection( item );
    assertSame( item, folder.getSelection() );
    assertEquals( 1, folder.getItemCount() );
    assertTrue( getChevronVisible( folder ) );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), getChevronRect( folder ) );

    // Chevron must again be visible after the last item was removed
    item.dispose();
    label.dispose();
    assertEquals( 0, folder.getItemCount() );
    assertTrue( getChevronVisible( folder ) );

    // Chevron must be visible when there is more than one item regardless of
    // selection
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    CTabItem item2 = new CTabItem( folder, SWT.NONE );
    assertTrue( folder.getItemCount() > 1 ); // test precondition
    assertEquals( null, folder.getSelection() ); // test precondition
    assertTrue( getChevronVisible( folder ) );
    folder.setSelection( 0 );
    assertEquals( 0, folder.getSelectionIndex() );  // test precondition
    assertTrue( getChevronVisible( folder ) );
    // Clean up
    item1.dispose();
    item2.dispose();
  }

  @Test
  public void testComputeTrim() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Rectangle expected = new Rectangle( -2, -29, 4, 31 );
    assertEquals( expected, folder.computeTrim( 0, 0, 0, 0 ) );

    new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
    expected = new Rectangle( -2, -33, 4, 35 );
    assertEquals( expected, folder.computeTrim( 0, 0, 0, 0 ) );

    folder.setMinimized( true );
    expected = new Rectangle( -2, -33, 4, 33 );
    assertEquals( expected, folder.computeTrim( 0, 0, 0, 0 ) );

    folder = new CTabFolder( shell, SWT.FLAT );
    expected = new Rectangle( 0, -27, 0, 27 );
    assertEquals( expected, folder.computeTrim( 0, 0, 0, 0 ) );

    folder = new CTabFolder( shell, SWT.BORDER );
    expected = new Rectangle( -3, -29, 6, 32 );
    assertEquals( expected, folder.computeTrim( 0, 0, 0, 0 ) );
  }

  @Test
  public void testClientArea() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
    folder.setSize( 100, 100 );

    Rectangle expected = new Rectangle( 2, 33, 96, 65 );
    assertEquals( expected, folder.getClientArea() );

    folder.setMinimized( true );
    expected = new Rectangle( 2, 33, 0, 0 );
    assertEquals( expected, folder.getClientArea() );

    folder = new CTabFolder( shell, SWT.FLAT );
    folder.setSize( 100, 100 );
    expected = new Rectangle( 0, 27, 100, 73 );
    assertEquals( expected, folder.getClientArea() );

    folder = new CTabFolder( shell, SWT.BORDER );
    folder.setSize( 100, 100 );
    expected = new Rectangle( 3, 29, 94, 68 );
    assertEquals( expected, folder.getClientArea() );
  }

  @Test
  public void testComputeSize() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    assertEquals( new Point( 0, 0 ), folder.getSize() );
    Point expected = new Point( 7, 95 );
    assertEquals( expected, folder.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    CTabItem item2 = new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
    expected = new Point( 97, 99 );
    assertEquals( expected, folder.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    Button content1 = new Button( folder, SWT.PUSH );
    content1.setText( "Content for tab 1" );
    item1.setControl( content1 );
    expected = new Point( 150, 65 );
    assertEquals( expected, folder.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    Label content2 = new Label( folder, SWT.NONE );
    content2.setText( "Content for tab 2 which is wider." );
    item2.setControl( content2 );
    expected = new Point( 226, 65 );
    assertEquals( expected, folder.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    Rectangle trimExpected = new Rectangle( -2, -33, 4, 35 );
    assertEquals( trimExpected, folder.computeTrim( 0, 0, 0, 0 ) );
    expected = new Point( 304, 335 );
    assertEquals( expected, folder.computeSize( 300, 300 ) );
  }

  @Test
  public void testGetItem() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    item1.setText( "abc" );

    assertNull( folder.getItem( new Point( 1000, 80 ) ) );
    assertNull( folder.getItem( new Point( 10, 7 ) ) );

    folder.setSize( 800, 800 );
    assertSame( item1, folder.getItem( new Point( 10, 7 ) ) );
  }

  @Test
  public void testSetSelectionBackground() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Color color = new Color( display, 0, 0, 0 );
    color.dispose();
    try {
      folder.setSelectionBackground( color );
      fail( "Disposed Image must not be set." );
    } catch( IllegalArgumentException e ) {
      // Expected Exception
    }
  }

  @Test
  public void testSetSelectionBackgroundI() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    Color color = new Color( display, 255, 0, 0 );
    color.dispose();
    Color[] colors = new Color[]{
      new Color( display, 0, 0, 0 ),
      color,
      new Color( display, 0, 0, 255 )
    };
    int[] percents = new int[]{ 10, 40, 50 };
    try {
      folder.setSelectionBackground( colors, percents, true );
      fail( "Disposed Image must not be set." );
    } catch( IllegalArgumentException e ) {
      // Expected Exception
    }
  }

  // bug 300998
  @Test
  public void testRemoveLastItem() {
    shell.open();
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    CTabItem item = new CTabItem( folder, SWT.NONE );
    item.setControl( new Button( folder, SWT.PUSH ) );
    item.getControl().setVisible( true );
    item.getControl().forceFocus();
    assertSame( item.getControl(), display.getFocusControl() ); // precondition
    folder.setSelection( item );
    try {
      item.dispose();
    } catch( Throwable e ) {
      e.printStackTrace();
      fail( "Disposing last item that contains focused control must not fail" );
    }
  }

  @Test
  public void testDisposeWithFontDisposeInDisposeListener() {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
    new CTabItem( folder, SWT.NONE );
    final Font font = new Font( display, "font-name", 10, SWT.NORMAL );
    folder.setFont( font );
    folder.addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent event ) {
        font.dispose();
      }
    } );
    folder.dispose();
  }

  @Test
  public void testIsSerializable() throws Exception {
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    CTabItem item = new CTabItem( folder, SWT.NONE );
    item.setText( "item1" );

    CTabFolder deserializedFolder = Fixture.serializeAndDeserialize( folder );

    assertEquals( folder.getItemCount(), deserializedFolder.getItemCount() );
    assertSame( deserializedFolder, deserializedFolder.getItem( 0 ).getParent() );
    assertEquals( item.getText(), deserializedFolder.getItem( 0 ).getText() );
  }

  @Test
  public void testAddCTabFolder2ListenerWithNullArgument() {
    CTabFolder tabFolder = new CTabFolder( shell, SWT.NONE );

    try {
      tabFolder.addCTabFolder2Listener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testAddCTabFolder2Listener() {
    CTabFolder tabFolder = new CTabFolder( shell, SWT.NONE );

    tabFolder.addCTabFolder2Listener( mock( CTabFolder2Listener.class ) );

    assertTrue( tabFolder.isListening( EventTypes.CTAB_FOLDER_CLOSE ) );
    assertTrue( tabFolder.isListening( EventTypes.CTAB_FOLDER_MINIMIZE ) );
    assertTrue( tabFolder.isListening( EventTypes.CTAB_FOLDER_MAXIMIZE ) );
    assertTrue( tabFolder.isListening( EventTypes.CTAB_FOLDER_RESTORE ) );
    assertTrue( tabFolder.isListening( EventTypes.CTAB_FOLDER_SHOW_LIST ) );
  }

  @Test
  public void testRemoveCTabFolder2ListenerWithNullArgument() {
    CTabFolder tabFolder = new CTabFolder( shell, SWT.NONE );

    try {
      tabFolder.removeCTabFolder2Listener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testRemoveCTabFolder2Listener() {
    CTabFolder tabFolder = new CTabFolder( shell, SWT.NONE );
    CTabFolder2Listener listener = mock( CTabFolder2Listener.class );
    tabFolder.addCTabFolder2Listener( listener );

    tabFolder.removeCTabFolder2Listener( listener );

    assertFalse( tabFolder.isListening( EventTypes.CTAB_FOLDER_CLOSE ) );
    assertFalse( tabFolder.isListening( EventTypes.CTAB_FOLDER_MINIMIZE ) );
    assertFalse( tabFolder.isListening( EventTypes.CTAB_FOLDER_MAXIMIZE ) );
    assertFalse( tabFolder.isListening( EventTypes.CTAB_FOLDER_RESTORE ) );
    assertFalse( tabFolder.isListening( EventTypes.CTAB_FOLDER_SHOW_LIST ) );
  }

  @Test
  public void testAddSelectionListenerWithNullArgument() {
    CTabFolder tabFolder = new CTabFolder( shell, SWT.NONE );

    try {
      tabFolder.addSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testAddSelectionListener() {
    CTabFolder tabFolder = new CTabFolder( shell, SWT.NONE );

    tabFolder.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( tabFolder.isListening( SWT.Selection ) );
    assertTrue( tabFolder.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener() {
    CTabFolder tabFolder = new CTabFolder( shell, SWT.NONE );
    SelectionListener listener = mock( SelectionListener.class );
    tabFolder.addSelectionListener( listener );

    tabFolder.removeSelectionListener( listener );

    assertFalse( tabFolder.isListening( SWT.Selection ) );
    assertFalse( tabFolder.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListenerWithNullArgument() {
    CTabFolder tabFolder = new CTabFolder( shell, SWT.NONE );

    try {
      tabFolder.removeSelectionListener( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  private static Rectangle getChevronRect( CTabFolder folder ) {
    Object adapter = folder.getAdapter( ICTabFolderAdapter.class );
    ICTabFolderAdapter folderAdapter = ( ICTabFolderAdapter )adapter;
    return folderAdapter.getChevronRect();
  }

  private static boolean getChevronVisible( CTabFolder folder ) {
    Object adapter = folder.getAdapter( ICTabFolderAdapter.class );
    ICTabFolderAdapter folderAdapter = ( ICTabFolderAdapter )adapter;
    return folderAdapter.getChevronVisible();
  }

}
