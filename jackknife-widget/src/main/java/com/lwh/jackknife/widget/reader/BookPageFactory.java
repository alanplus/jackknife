package com.lwh.jackknife.widget.reader;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.util.Log;

import com.lwh.jackknife.widget.R;
import com.lwh.jackknife.widget.util.DensityUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class BookPageFactory {

    private StringBuilder mBookContent;
    private Context mContext;
    private static final String TAG = "BookPageFactory";
    private File mBookFile = null;
    private int mBackgroundColor = 0xffff9e85; // 背景颜色
    private Bitmap mBookBg = null;
    private int mFontSize;
    private boolean mFirstPage, mLastPage;
    private Vector<String> mLines = new Vector<>();
    private MappedByteBuffer mBuffer = null;// 内存中的图书字符
    private int mBufferBegin = 0;// 当前页起始位置
    private int mBufferEnd = 0;// 当前页终点位置

    private static int mBufferLength = 0; // 图书总长度
    private static List<String> mBookCatalogue = new ArrayList<>();
    private static List<Integer> mBookCatalogueStartPos = new ArrayList<>();
    private String mContentCharset = "UTF-8";
    private int mStartPos = 0;
    private int mTextColor = Color.rgb(50, 65, 78);
    private static Typeface mTypeface;
    private int mMarginHeight; // 上下与边缘的距离
    private int mMarginWidth; // 左右与边缘的距离
    private int mHeight;
    private int mLineCount; // 每页可以显示的行数
    private Paint mPaint;

    private SimpleDateFormat mSimpleDateFormat;
    private String mDate;
    private DecimalFormat mDecimalFormat;

    private float mVisibleHeight; // 绘制内容的宽
    private float mVisibleWidth; // 绘制内容的宽
    private int mWidth;
    private Intent mBatteryInfoIntent;

    private Paint mBatteryPaint;
    private float mBorderWidth;
    private RectF mRect1 = new RectF();
    private RectF mRect2 = new RectF();

    public BookPageFactory(int w, int h, Context context) {
        mWidth = w;
        mHeight = h;
        mContext = context;
        mFontSize = (int) context.getResources().getDimension(R.dimen.book_reader_view_default_text_size);
        mSimpleDateFormat = new SimpleDateFormat("HH:mm");
        mDate = mSimpleDateFormat.format(new java.util.Date());
        mDecimalFormat = new DecimalFormat("#0.0");
        mBorderWidth = context.getResources().getDimension(R.dimen.book_reader_view_board_battery_border_width);
        mMarginWidth = (int) context.getResources().getDimension(R.dimen.book_reader_view_margin_width);
        mMarginHeight = (int) context.getResources().getDimension(R.dimen.book_reader_view_margin_height);
        mTypeface = Typeface.createFromAsset(context.getAssets(), "font/QH.ttf");
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTextSize(mFontSize);
        mPaint.setColor(mTextColor);
        mPaint.setTypeface(mTypeface);
        mPaint.setSubpixelText(true);
        mVisibleWidth = mWidth - mMarginWidth * 2;
        mVisibleHeight = mHeight - mMarginHeight * 2;
        mLineCount = (int) (mVisibleHeight / mFontSize) - 1; // 可显示的行数,-1是因为底部显示进度的位置容易被遮住
        mBatteryInfoIntent = context.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));//注册广播,随时获取到电池电量信息
        mBatteryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBatteryPaint.setTextSize(DensityUtils.sp2px(12, context));
        mBatteryPaint.setTypeface(mTypeface);
        mBatteryPaint.setTextAlign(Paint.Align.LEFT);
        mBatteryPaint.setColor(mTextColor);
        mBookContent = new StringBuilder();
    }

    public void onDraw(Canvas canvas) {
        int size = getFontSize();
        mPaint.setTextSize(size);
        mPaint.setColor(mTextColor);
        if (mLines.size() == 0)
            mLines = getNextPage();
        if (mLines.size() > 0) {
            if (mBookBg == null)
                canvas.drawColor(mBackgroundColor);
            else
                canvas.drawBitmap(mBookBg, 0, 0, null);
            int y = mMarginHeight;
            for (String line : mLines) {
                y += mFontSize;
                canvas.drawText(line, mMarginWidth, y, mPaint);
                mBookContent.append(line);
            }
            mBookContent = null;
        }
        //画进度及时间
        int dateWith = (int) (mBatteryPaint.measureText(mDate) + mBorderWidth);
        float fPercent = (float) (mBufferBegin * 1.0 / mBufferLength);
        String strPercent = mDecimalFormat.format(fPercent * 100) + "%";
        int nPercentWidth = (int) mBatteryPaint.measureText("999.9%") + 1;  //Paint.measureText直接返回參數字串所佔用的寬度
        canvas.drawText(strPercent, mWidth - nPercentWidth, mHeight - 10, mBatteryPaint);//x y为坐标值
        canvas.drawText(mDate, mMarginWidth, mHeight - 10, mBatteryPaint);

        // 画电池
        int level = mBatteryInfoIntent.getIntExtra("level", 0);
        int scale = mBatteryInfoIntent.getIntExtra("scale", 100);
        float mBatteryPercentAge = (float) level / scale;
        int rect1Left = mMarginWidth + dateWith;//电池外框left位置

        //画电池外框
        float width = DensityUtils.dp2px(20, mContext) - mBorderWidth;
        float height = DensityUtils.dp2px(20, mContext);
        mRect1.set(rect1Left, mHeight - height - 10, rect1Left + width, mHeight - 10);
        mRect2.set(rect1Left + mBorderWidth, mHeight - height + mBorderWidth - 10, rect1Left + width - mBorderWidth, mHeight - mBorderWidth - 10);
        canvas.save();
        canvas.clipRect(mRect2, Region.Op.DIFFERENCE);
        canvas.drawRect(mRect1, mBatteryPaint);
        canvas.restore();
        //画电量部分
        mRect2.left += mBorderWidth;
        mRect2.right -= mBorderWidth;
        mRect2.right = mRect2.left + mRect2.width() * mBatteryPercentAge;
        mRect2.top += mBorderWidth;
        mRect2.bottom -= mBorderWidth;
        canvas.drawRect(mRect2, mBatteryPaint);
        //画电池头
        int poleHeight = (int) DensityUtils.dp2px(10, mContext) / 2;
        mRect2.left = mRect1.right;
        mRect2.top = mRect2.top + poleHeight / 4;
        mRect2.right = mRect1.right + mBorderWidth;
        mRect2.bottom = mRect2.bottom - poleHeight / 4;
        canvas.drawRect(mRect2, mBatteryPaint);
    }

    /**
     * 根据
     *
     * @param path  书籍路径
     * @param begin 表示书签记录的位置，读取书签时，将begin值给mBufferEnd，在读取nextPage，及成功读取到了书签
     *              记录时将mBufferBegin开始位置作为书签记录
     */
    @SuppressWarnings("resource")
    public void openBook(String path, int begin) throws IOException {
        mBookFile = new File(path);
        long lLen = mBookFile.length();
        mBufferLength = (int) lLen;
        mBuffer = new RandomAccessFile(mBookFile, "r").getChannel().map(
                FileChannel.MapMode.READ_ONLY, 0, lLen);
        // 设置已读进度
        if (begin >= 0) {
            mBufferBegin = begin;
            mBufferEnd = begin;
        } else {
        }
    }

    /**
     * 获取下一页的内容。
     */
    protected Vector<String> getNextPage() {
        mPaint.setTextSize(mFontSize);
        mPaint.setColor(mTextColor);
        String strParagraph = "";
        Vector<String> lines = new Vector<String>();
        while (lines.size() < mLineCount && mBufferEnd < mBufferLength) {
            byte[] paraBuf = readParagraphForward(mBufferEnd);
            mBufferEnd += paraBuf.length;// 每次读取后，记录结束点位置，该位置是段落结束位置
            try {
                strParagraph = new String(paraBuf, mContentCharset);// 转换成制定GBK编码
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "pageDown->转换编码失败", e);
            }
            String strReturn = "";
            // 替换掉回车换行符,防止段落发生错乱
            if (strParagraph.indexOf("\r\n") != -1) {   //windows
                strReturn = "\r\n";
                strParagraph = strParagraph.replaceAll("\r\n", "");
            } else if (strParagraph.indexOf("\n") != -1) {    //linux
                strReturn = "\n";
                strParagraph = strParagraph.replaceAll("\n", "");
            }

            if (strParagraph.length() == 0) {
                lines.add(strParagraph);
            }
            while (strParagraph.length() > 0) {
                // 画一行文字
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
                        null);
                lines.add(strParagraph.substring(0, nSize));
                strParagraph = strParagraph.substring(nSize);// 得到剩余的文字
                // 超出最大行数则不再画
                if (lines.size() >= mLineCount) {

                    break;
                }
            }
            lines.add("\n\n");//段落间加一个空白行
            // 如果该页最后一段只显示了一部分，则重新定位结束点位置
            if (strParagraph.length() != 0) {
                try {
                    mBufferEnd -= (strParagraph + strReturn)
                            .getBytes(mContentCharset).length;
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "pageDown->记录结束点位置失败", e);
                }
            }
        }
        return lines;
    }

    /**
     * 得到上上页的结束位置。
     */
    protected void getLastPage() {
        if (mBufferBegin < 0)
            mBufferBegin = 0;
        Vector<String> lines = new Vector<String>();
        String strParagraph = "";
        while (lines.size() < mLineCount && mBufferBegin > 0) {
            Vector<String> paraLines = new Vector<String>();
            byte[] paraBuf = readParagraphBack(mBufferBegin);
            mBufferBegin -= paraBuf.length;// 每次读取一段后,记录开始点位置,是段首开始的位置
            try {
                strParagraph = new String(paraBuf, mContentCharset);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "pageUp->转换编码失败", e);
            }
            String strReturn = "";
            strParagraph = strParagraph.replaceAll("\r\n", "");
            strParagraph = strParagraph.replaceAll("\n", "");
            // 如果是空白行，直接添加
            if (strParagraph.length() == 0) {
                lines.add(strParagraph);
            }

            while (strParagraph.length() > 0) {
                // 画一行文字
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
                        null);
                paraLines.add(strParagraph.substring(0, nSize));
                strParagraph = strParagraph.substring(nSize);

            }
            lines.addAll(0, paraLines);
            lines.add("\n\n");

            if (lines.size() > mLineCount) {
                //  break;
            }
        }

        while (lines.size() > mLineCount) {
            try {
                mBufferBegin += lines.get(0).getBytes(mContentCharset).length;
                lines.remove(0);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "pageUp->记录起始点位置失败", e);
            }
        }
        mBufferEnd = mBufferBegin;// 上上一页的结束点等于上一页的起始点
        return;
    }

    /**
     * 向前翻页。
     *
     * @throws IOException
     */
    public void prevPage() throws IOException {
        if (mBufferBegin <= 0) {
            mBufferBegin = 0;
            mFirstPage = true;
            return;
        } else
            mFirstPage = false;
        mLines.clear();
        getLastPage();
        mLines = getNextPage();

    }

    /**
     * 向后翻页。
     *
     * @throws IOException
     */
    public void nextPage() throws IOException {
        if (mBufferEnd >= mBufferLength) {
            mLastPage = true;
            return;
        } else
            mLastPage = false;
        mLines.clear();
        mBufferBegin = mBufferEnd;// 当前页结束位置作为向前翻页的开始位置
        mLines = getNextPage();

    }

    public void currentPage() throws IOException {
        mLines.clear();
        mLines = getNextPage();
    }

    /**
     * 读取指定位置的上一个段落。
     *
     * @param nFromPos
     * @return byte[]
     */
    protected byte[] readParagraphBack(int nFromPos) {
        int nEnd = nFromPos;
        int i;
        byte b0, b1;
        if (mContentCharset.equals("UTF-16LE")) {
            i = nEnd - 2;
            while (i > 0) {
                b0 = mBuffer.get(i);
                b1 = mBuffer.get(i + 1);
                if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
                    i += 2;
                    break;
                }
                i--;
            }

        } else if (mContentCharset.equals("UTF-16BE")) {
            i = nEnd - 2;
            while (i > 0) {
                b0 = mBuffer.get(i);
                b1 = mBuffer.get(i + 1);
                if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
                    i += 2;
                    break;
                }
                i--;
            }
        } else {
            i = nEnd - 1;
            while (i > 0) {
                b0 = mBuffer.get(i);
                if (b0 == 0x0a && i != nEnd - 1) {// 0x0a表示换行符
                    i++;
                    break;
                }
                i--;
            }
        }
        if (i < 0)
            i = 0;
        int nParaSize = nEnd - i;
        int j;
        byte[] buf = new byte[nParaSize];
        for (j = 0; j < nParaSize; j++) {
            buf[j] = mBuffer.get(i + j);
        }
        return buf;
    }

    /**
     * 读取指定位置的下一个段落。
     *
     * @param nFromPos
     * @return byte[]
     */
    protected byte[] readParagraphForward(int nFromPos) {
        int nStart = nFromPos;
        int i = nStart;
        byte b0, b1;
        // 根据编码格式判断换行
        if (mContentCharset.equals("UTF-16LE")) {
            while (i < mBufferLength - 1) {
                b0 = mBuffer.get(i++);
                b1 = mBuffer.get(i++);
                if (b0 == 0x0a && b1 == 0x00) {
                    break;
                }
            }
        } else if (mContentCharset.equals("UTF-16BE")) {
            while (i < mBufferLength - 1) {
                b0 = mBuffer.get(i++);
                b1 = mBuffer.get(i++);
                if (b0 == 0x00 && b1 == 0x0a) {
                    break;
                }
            }
        } else {
            while (i < mBufferLength) {
                b0 = mBuffer.get(i++);
                if (b0 == 0x0a) {
                    break;
                }
            }
        }
        int nParaSize = i - nStart; //段落长度
        byte[] buf = new byte[nParaSize];
        for (i = 0; i < nParaSize; i++) {
            buf[i] = mBuffer.get(nFromPos + i);
        }
        return buf;
    }

    public void setBgBitmap(Bitmap bitmap) {
        mBookBg = bitmap;
    }

    public void setFontSize(int fontSize) {
        this.mFontSize = fontSize;
        mLineCount = (int) (mVisibleHeight / mFontSize) - 1;
    }

    public int getFontSize() {
        return this.mFontSize;
    }

    public void setBufferBegin(int begin) {
        this.mBufferBegin = begin;
    }

    public void setBufferEnd(int end) {
        this.mBufferEnd = end;
    }

    public int getBufferBegin() {
        return mBufferBegin;
    }

    public String getFirstTwoLineText() {
        return mLines.size() > 0 ? mLines.get(0) + mLines.get(1) : "";
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int color) {
        this.mTextColor = color;
    }

    public static int getBufferLength() {
        return mBufferLength;
    }

    public int getBufferEnd() {
        return mBufferEnd;
    }

    public int getLineCount() {
        return mLineCount;
    }

    public boolean isFirstPage() {
        return mFirstPage;
    }

    public boolean isLastPage() {
        return mLastPage;
    }

    public static List<Integer> getBookCatalogueStartPos() {
        return mBookCatalogueStartPos;
    }

    public static List<String> getBookCatalogue() {
        return mBookCatalogue;
    }
}
