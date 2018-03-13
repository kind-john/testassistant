package com.ckt.ckttestassistant.testitems;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by ckt on 18-1-31.
 */

public class AirPlaneSwitchOn extends TestItemBase {
    public static final int ID = 15;
    private static final String TITLE = "AirPlane Switch On";
    private static final String TAG = "AirPlaneSwitchOn";
    private static final String EXCEL_TITLE_RESULT = "result";
    private static final String EXCEL_TITLE_TOTAL_TIMES = "total times";
    private static final String EXCEL_TITLE_COMPLETED_TIMES = "completed times";
    private static final String EXCEL_TITLE_FAIL_TIMES = "fial times";

    private int mDelay = 0;

    public AirPlaneSwitchOn() {
        super();
        String className = this.getClass().getName();
        setClassName(className);
        setID(ID);
        setTitle(TITLE);
    }

    public AirPlaneSwitchOn(Context context) {
        super(context);
        String className = this.getClass().getName();
        setClassName(className);
        setID(ID);
        setTitle(TITLE);
    }

    public int getDelay() {
        return mDelay;
    }

    public void setDelay(int delay) {
        this.mDelay = delay;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public void saveTestData() {

    }

    @Override
    public boolean doExecute(UseCaseManager.ExecuteCallback executeCallback, boolean finish) {
        LogUtils.d(TAG, "AirPlaneSwitchOn doExecute");
        //do test,then close progressview
        if(finish && executeCallback != null){
            LogUtils.d(TAG, "stop test handler");
            executeCallback.stopTestHandler();
        }
        return false;
    }

    private void addRecordTitleToExcel(WritableSheet sheet, int row, int col) throws WriteException {
        String result;
        WritableFont font = new WritableFont(WritableFont.createFont("楷体"), 11, WritableFont.BOLD);
        WritableCellFormat format = new WritableCellFormat(font);

        Label label1 = new Label(col, row, EXCEL_TITLE_RESULT);
        sheet.addCell(label1);
        Label label2 = new Label(col + 1, row, EXCEL_TITLE_TOTAL_TIMES);
        sheet.addCell(label2);
        Label label3 = new Label(col + 2, row, EXCEL_TITLE_COMPLETED_TIMES);
        sheet.addCell(label3);
        Label label4 = new Label(col + 3, row, EXCEL_TITLE_FAIL_TIMES);
        sheet.addCell(label4);
    }

    private void addRecordToExcel(WritableSheet sheet, int row, int col) throws WriteException {
        String result;
        WritableCellFormat labelFormat = new WritableCellFormat();
        WritableCellFormat failFormat = new WritableCellFormat();
        if(isSuccess()){
            result = MyConstants.SUCCESS;
            labelFormat.setBackground(Colour.GREEN);
        }else{
            result = MyConstants.FAIL;
            labelFormat.setBackground(Colour.RED);
        }
        Label label = new Label(col, row, result);
        sheet.addCell(label);
        Number totalTimes = new Number(col + 1, row, mTimes);
        sheet.addCell(totalTimes);
        Number completedTimes = new Number(col + 2, row, mCompletedTimes);
        sheet.addCell(completedTimes);
        Number failTimes = new Number(col + 3, row, mFailTimes);
        if(mFailTimes > 0){
            failFormat.setBackground(Colour.RED);
        }
        sheet.addCell(failTimes);
    }
    @Override
    public void saveResult() {
        LogUtils.d(TAG, "AirPlaneSwitchOn saveResult");
        UseCaseManager usm = UseCaseManager.getInstance(mContext);
        String file = usm.getCurrentExcelFile();
        try {
            Workbook wb = Workbook.getWorkbook(new File(file));
            WritableWorkbook book = Workbook.createWorkbook(new File(file), wb);
            WritableSheet sheet = book.getSheet(mParent.getTitle());
            /*if(book.getNumberOfSheets() > 0){
                sheet = book.getSheet(0);
            }else{
                sheet = book.createSheet(""+mCompletedTimes, 0);
            }*/
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
                    addRecordToExcel(sheet, row + 2, col);
                }else{
                    LogUtils.d(TAG, "there is no cell of "+ getTitle()+", so create it.");
                    //找到空白地方插入label标记
                    //int emptyRow = ExcelUtils.findEmptyRowFromSheet(sheet, 2, 1);
                    int rows = sheet.getRows();
                    LogUtils.d(TAG, "rows = " + rows);
                    label = new Label(0, rows, getTitle(), format);
                    sheet.addCell(label);
                    addRecordTitleToExcel(sheet, rows + 1, 0);
                    //sheet.insertRow(emptyRow + 2);
                    addRecordToExcel(sheet, rows + 2, 0);
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

        builder.setTitle("AirPlaneSwitchOn settings")
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
                                mUseCaseManager.updateTestItemOfAllUseCaseXml(AirPlaneSwitchOn.this);
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
