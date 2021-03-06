/*******************************************************************************
 * Copyright (c) 2011, 2014 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.textsize;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ControlUtil;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TextSizeRecalculation_Test {

  private static final FontData FONT_DATA = new FontData( "arial", 23, SWT.BOLD );
  private static final String TEXT_TO_MEASURE = "textToMeasure";

  private Display display;
  private Shell shell;
  private Composite scrolledCompositeContent;
  private ResizeListener shellResizeListener;
  private ResizeListener scrolledCompositeContentResizeListener;
  private Label packedControl;
  private TableColumn packedTableColumn;
  private TreeColumn packedTreeColumn;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testExecute() {
    createWidgetTree();
    registerResizeListeners();
    turnOnImmediateResizeEventHandling();
    fakeMeasurementResults();

    TextSizeRecalculation.execute();

    checkResizeTookPlace();
    checkRePackTookPlace();
  }

  /*
   * See bug 421942: [Dialog] Initial size is too big
   */
  @Test
  public void testExecute_restoresShellAfterScrolledComposite() {
    createWidgetTree();
    turnOnImmediateResizeEventHandling();
    fakeMeasurementResults();
    final Rectangle scrolledCompositeContentBounds = new Rectangle( 0, 0, 0, 0 );
    shell.addListener( SWT.Resize, new Listener() {
      public void handleEvent( Event event ) {
        Rectangle size = scrolledCompositeContent.getBounds();
        scrolledCompositeContentBounds.x = size.x;
        scrolledCompositeContentBounds.y = size.x;
        scrolledCompositeContentBounds.width = size.width;
        scrolledCompositeContentBounds.height = size.height;
      }
    } );

    TextSizeRecalculation.execute();

    assertEquals( getInitialContentBounds(), scrolledCompositeContentBounds );
  }

  @Test
  public void testIControlAdapterIsPacked() {
    Shell control = new Shell( display );
    assertFalse( ControlUtil.getControlAdapter( control ).isPacked() );

    control.pack();
    assertTrue( ControlUtil.getControlAdapter( control ).isPacked() );

    control.setBounds( new Rectangle( 1, 1, 2, 2 ) );
    assertFalse( ControlUtil.getControlAdapter( control ).isPacked() );
  }

  @Test
  public void testShellRePackTookPlace() {
    shell.pack();
    turnOnImmediateResizeEventHandling();
    fakeMeasurementResults();

    TextSizeRecalculation.execute();

    assertTrue( ControlUtil.getControlAdapter( shell ).isPacked() );
  }

  @Test
  public void testLayoutOfCompositeWithFixedSize() {
    turnOnImmediateResizeEventHandling();
    createShellWithLayout();
    Composite fixedSizeComposite = createFixedSizeComposite();
    Label label = new Label( fixedSizeComposite, SWT.NONE );
    label.setFont( new Font( display, FONT_DATA ) );
    label.setText( TEXT_TO_MEASURE );
    shell.pack();
    ResizeListener resizeListener = new ResizeListener();
    label.addControlListener( resizeListener );
    fakeMeasurementResults();

    TextSizeRecalculation.execute();

    assertEquals( 1, resizeListener.resizeCount() );
    assertEquals( "false|", resizeListener.getResizeLog() );
  }

  private void checkResizeTookPlace() {
    assertEquals( getInitialShellBounds(), shell.getBounds() );
    assertEquals( getInitialContentBounds(), scrolledCompositeContent.getBounds() );
    assertEquals( 2, shellResizeListener.resizeCount() );
    assertEquals( 2, scrolledCompositeContentResizeListener.resizeCount() );
    assertEquals( "true|false|", shellResizeListener.getResizeLog() );
    assertEquals( "true|false|", scrolledCompositeContentResizeListener.getResizeLog() );
  }

  private void checkRePackTookPlace() {
    assertEquals( new Point( 100, 22 ), packedControl.getSize() );
    assertEquals( 106, packedTableColumn.getWidth() );
    assertEquals( 116, packedTreeColumn.getWidth() );
  }

  private Rectangle getInitialShellBounds() {
    return new Shell().getBounds();
  }

  private void createWidgetTree() {
    createShellWithLayout();
    createScrolledCompositeWithContent();
    createPackedControl();
    createPackedColumns();
  }

  private void createPackedControl() {
    packedControl = new Label( scrolledCompositeContent, SWT.NONE );
    packedControl.setFont( new Font( display, FONT_DATA ) );
    packedControl.setText( TEXT_TO_MEASURE );
    packedControl.pack();
  }

  private void createPackedColumns() {
    Table table = new Table( scrolledCompositeContent, SWT.NONE );
    table.setHeaderVisible( true );
    table.setFont( new Font( display, FONT_DATA ) );
    packedTableColumn = new TableColumn( table, SWT.NONE );
    packedTableColumn.setText( TEXT_TO_MEASURE );
    packedTableColumn.pack();

    Tree tree = new Tree( scrolledCompositeContent, SWT.NONE );
    tree.setHeaderVisible( true );
    tree.setFont( new Font( display, FONT_DATA ) );
    packedTreeColumn = new TreeColumn( tree, SWT.NONE );
    packedTreeColumn.setText( TEXT_TO_MEASURE );
    packedTreeColumn.pack();
  }

  private void createScrolledCompositeWithContent() {
    ScrolledComposite scrolledComposite = new ScrolledComposite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    scrolledCompositeContent = new Composite( scrolledComposite, SWT.NONE );
    scrolledCompositeContent.setBounds( getInitialContentBounds() );
    scrolledComposite.setContent( scrolledCompositeContent );
    scrolledComposite.setOrigin( 5, 5 );
  }

  private Rectangle getInitialContentBounds() {
    return new Rectangle( -5, -5, 100, 100 );
  }

  private void createShellWithLayout() {
    shell.setLayout( new GridLayout() );
  }

  private Composite createFixedSizeComposite() {
    Composite result = new Composite( shell, SWT.NONE );
    result.setLayout( new GridLayout() );
    result.setLayoutData( new GridData( 300, 50 ) );
    return result;
  }

  private void registerResizeListeners() {
    shellResizeListener = new ResizeListener();
    scrolledCompositeContentResizeListener = new ResizeListener();
    shell.addControlListener( shellResizeListener );
    scrolledCompositeContent.addControlListener( scrolledCompositeContentResizeListener );
  }

  private void turnOnImmediateResizeEventHandling() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  private void fakeMeasurementResults() {
    ProbeResultStore.getInstance().createProbeResult( new Probe( FONT_DATA ), new Point( 4, 20 ) );
    getApplicationContext().getTextSizeStorage().storeFont( FONT_DATA );
    TextSizeStorageUtil.store( FONT_DATA,
                               TEXT_TO_MEASURE,
                               SWT.DEFAULT,
                               TextSizeUtil.TEXT_EXTENT,
                               new Point( 100, 20 ) );
    TextSizeStorageUtil.store( FONT_DATA,
                               TEXT_TO_MEASURE,
                               SWT.DEFAULT,
                               TextSizeUtil.STRING_EXTENT,
                               new Point( 100, 20 ) );
  }

  private final class ResizeListener implements ControlListener {
    private int resizeCount;
    private final StringBuilder resizeLog = new StringBuilder();

    public void controlResized( ControlEvent e ) {
      resizeCount++;
      resizeLog.append( TextSizeUtil.isTemporaryResize() );
      resizeLog.append( "|" );
    }

    public void controlMoved( ControlEvent e ) {
    }

    public int resizeCount() {
      return resizeCount;
    }

    public String getResizeLog() {
      return resizeLog.toString();
    }
  }

}
