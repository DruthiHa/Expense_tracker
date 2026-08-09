package com.finaudit.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.finaudit.domain.model.MerchantMapping;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MerchantMappingDao_Impl implements MerchantMappingDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MerchantMapping> __insertionAdapterOfMerchantMapping;

  public MerchantMappingDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMerchantMapping = new EntityInsertionAdapter<MerchantMapping>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `merchant_mappings` (`merchantNormalized`,`category`,`source`,`count`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MerchantMapping entity) {
        statement.bindString(1, entity.getMerchantNormalized());
        statement.bindString(2, entity.getCategory());
        statement.bindString(3, entity.getSource());
        statement.bindLong(4, entity.getCount());
      }
    };
  }

  @Override
  public Object insertMapping(final MerchantMapping mapping,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMerchantMapping.insert(mapping);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getMapping(final String normalized,
      final Continuation<? super MerchantMapping> $completion) {
    final String _sql = "SELECT * FROM merchant_mappings WHERE merchantNormalized = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, normalized);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MerchantMapping>() {
      @Override
      @Nullable
      public MerchantMapping call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMerchantNormalized = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantNormalized");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfCount = CursorUtil.getColumnIndexOrThrow(_cursor, "count");
          final MerchantMapping _result;
          if (_cursor.moveToFirst()) {
            final String _tmpMerchantNormalized;
            _tmpMerchantNormalized = _cursor.getString(_cursorIndexOfMerchantNormalized);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _result = new MerchantMapping(_tmpMerchantNormalized,_tmpCategory,_tmpSource,_tmpCount);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
