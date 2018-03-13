package com.ckt.ckttestassistant.testitems;

import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.ckt.ckttestassistant.R;
import com.ckt.ckttestassistant.UseCaseManager;
import com.ckt.ckttestassistant.utils.ExcelUtils;
import com.ckt.ckttestassistant.utils.LogUtils;
import com.ckt.ckttestassistant.utils.MyConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by ckt on 18-1-31.
 */

public class SimulateBackKey extends TestItemBase {
    public static final int ID = 47;
    private static final String TITLE = "Simulate Back Key";
    private static final String TAG = "SimulateBackKey";
    private String[] mExcelTitles = {
            "result",
            "total times",
            "completed times",
            "fial times"
    };

    public SimulateBackKey() {
        super();
        String className = this.getClass().getName();
        setClassName(className);
        setID(ID);
        setTitle(TITLE);
    }

    public SimulateBackKey(Context context) {
        super(context);
        String className = this.getClass().getName();
        setClassName(className);
        setID(ID);
        setTitle(TITLE);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public void saveTestData() {

    }
    /*private void sendDownEvents(int keyCode) {
        final long token = Binder.clearCallingIdentity();

        // Inject down.
        final long downTime = SystemClock.uptimeMillis();
        KeyEvent down = KeyEvent.obtain(downTime, downTime, KeyEvent.ACTION_DOWN, keyCode, 0, 0,
                KeyCharacterMap.VIRTUAL_KEYBOARD, 0, KeyEvent.FLAG_FROM_SYSTEM,
                InputDevice.SOURCE_KEYBOARD, null);
        InputManager.getInstance().injectInputEvent(down,
                InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
        down.recycle();

        Binder.restoreCallingIdentity(token);
    }

    private void sendUpEvents(int keyCode) {
        final long token = Binder.clearCallingIdentity();

        // Inject down.
        final long downTime = SystemClock.uptimeMillis();
        KeyEvent up = KeyEvent.obtain(downTime, downTime, KeyEvent.ACTION_UP, keyCode, 0, 0,
                KeyCharacterMap.VIRTUAL_KEYBOARD, 0, KeyEvent.FLAG_FROM_SYSTEM,
                InputDevice.SOURCE_KEYBOARD, null);
        InputManager.getInstance().injectInputEvent(up,
                InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
        up.recycle();

        Binder.restoreCallingIdentity(token);
    }*/
    @Override
    public boolean doExecute(UseCaseManager.ExecuteCallback executeCallback, boolean finish) {
        LogUtils.d(TAG, "SimulateBackKey doExecute");
        new Thread() {
            public void run() {
                try {
                /*Thread.sleep(100); // TouchEvent
                long now = SystemClock.uptimeMillis();
                KeyEvent down = new KeyEvent(now, now,
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK, 0);
                KeyEvent up = new KeyEvent(now, now, KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_BACK, 0);

                InputManager.getInstance().injectInputEvent(down,
                        InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
                InputManager.getInstance().injectInputEvent(up,
                        InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);*/
                    try {
                        Instrumentation inst = new Instrumentation();
                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        task2(true);
        //do test,then close progressview
        if(finish && executeCallback != null){
            LogUtils.d(TAG, "stop test handler");
            executeCallback.stopTestHandler();
        }
        return false;
    }

    @Override
    public void saveResult() {
        LogUtils.d(TAG, "SimulateBackKey saveResult");
        UseCaseManager usm = UseCaseManager.getInstance(mContext);
        String file = usm.getCurrentExcelFile();
        try {
            Workbook wb = Workbook.getWorkbook(new File(file));
            WritableWorkbook book = Workbook.createWorkbook(new File(file), wb);
            WritableSheet sheet = book.getSheet(mParent.getTitle());
            if(sheet == null){
                LogUtils.e(TAG, "sheet can not find : "+ mParent.getTitle());
                return ;
            }else{
                WritableFont font = new WritableFont(WritableFont.createFont("楷体"), 11, WritableFont.BOLD);
                WritableCellFormat format = new WritableCellFormat(font);
                Cell cell = sheet.findCell(getTitle());
                Label label;
                if(cell != null){
                    LogUtils.d(TAG, "found cell of "+ getTitle()+", insert record!");
                    LogUtils.d(TAG, "rows = " + sheet.getRows());
                    //找到合适的地方插入一行记录
                    int row, col;
                    row = cell.getRow();
                    col = cell.getColumn();
                    //在标题后插入一行
                    sheet.insertRow(row + 2);
                    //在添加的新空行写入数据
                    ExcelUtils.addRecordToExcel(sheet, row + 2, col, this);
                }else{
                    LogUtils.d(TAG, "there is no cell of "+ getTitle()+", so create it.");
                    //找到空白地方插入label标记
                    //int emptyRow = ExcelUtils.findEmptyRowFromSheet(sheet, 2, 1);
                    int rows = sheet.getRows();
                    LogUtils.d(TAG, "rows = " + rows);
                    label = new Label(0, rows, getTitle(), format);
                    sheet.addCell(label);
                    ExcelUtils.addRecordTitleToExcel(sheet, rows + 1, 0, mExcelTitles);
                    //sheet.insertRow(emptyRow + 2);
                    ExcelUtils.addRecordToExcel(sheet, rows + 2, 0, this);
                }
                book.write();
                book.close();
                wb.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showPropertyDialog(Context context, final boolean isNeedUpdateXml) {
        LogUtils.d(TAG, "showPropertyDialog :"+this.getClass().getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.settings_wifi_switch_on, null);
        final EditText delayEditText = (EditText)v.findViewById(R.id.delay);
        delayEditText.setText(String.valueOf(getDelay()));
        final EditText timesEditText = (EditText)v.findViewById(R.id.times);
        timesEditText.setText(String.valueOf(getTimes()));

        builder.setTitle("SimulateBackKey settings")
                .setView(v)
                .setMessage("for test")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtils.d(TAG, "Positive onClick");
                        int delay = Integer.parseInt(delayEditText.getText().toString());
                        int times = Integer.parseInt(timesEditText.getText().toString());
                        LogUtils.d(TAG, "delay = "+delay+"; times = "+times);
                        if(delay >= 0 && times > 0){
                            setDelay(delay);
                            setTimes(times);
                            if(isNeedUpdateXml){
                                mUseCaseManager.updateTestItemOfAllUseCaseXml(SimulateBackKey.this);
                            }
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtils.d(TAG, "Negative onClick");
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        LogUtils.d(TAG, "onDismiss");
                    }
                }).create().show();
    }

    @Override
    public void saveParameters(Document doc, Element element) {
        Element e1 = doc.createElement(MyConstants.XMLTAG_TESTITEM_DELAY);
        Node n1 = doc.createTextNode(""+mDelay);
        e1.appendChild(n1);
        element.appendChild(e1);
    }

    @Override
    public void saveParametersToXml(XmlSerializer serializer) throws Exception {
        try{
            //eg. start
            serializer.startTag(null, MyConstants.XMLTAG_TESTITEM_DELAY);
            serializer.text(""+mDelay);
            serializer.endTag(null, MyConstants.XMLTAG_TESTITEM_DELAY);
            //eg. end
        }catch (Exception e) {
            throw new Exception();
        }
    }
}
