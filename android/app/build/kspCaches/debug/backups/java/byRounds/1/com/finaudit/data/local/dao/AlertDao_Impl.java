package com.finaudit.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.finaudit.domain.model.Alert;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AlertDao_Impl implements AlertDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Alert> __insertionAdapterOfAlert;

  private final EntityDeletionOrUpdateAdapter<Alert> __updateAdapterOfAlert;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public AlertDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAlert = new EntityInsertionAdapter<Alert>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `alerts` (`id`,`type`,`message`,`actionScreen`,`createdAt`,`isDismissed`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Alert entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getType());
        statement.bindString(3, entity.getMessage());
        statement.bindString(4, entity.getActionScreen());
        statement.bindLong(5, entity.getCreatedAt());
        final int _tmp = entity.isDismissed() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
    this.__updateAdapterOfAlert = new EntityDeletionOrUpdateAdapter<Alert>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `alerts` SET `id` = ?,`type` = ?,`message` = ?,`actionScreen` = ?,`createdAt` = ?,`isDismissed` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Alert entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getType());
        statement.bindString(3, entity.getMessage());
        statement.bindString(4, entity.getActionScreen());
        statement.bindLong(5, entity.getCreatedAt());
        final int _tmp = entity.isDismissed() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM alerts";
        return _query;
      }
    };
  }

  @Override
  public Object insertAlert(final Alert alert, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAlert.insertAndReturnId(alert);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAlert(final Alert alert, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAlert.handle(alert);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Alert>> getActiveAlertsFlow() {
    final String _sql = "SELECT * FROM alerts WHERE isDismissed = 0 ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"alerts"}, new Callable<List<Alert>>() {
      @Override
      @NonNull
      public List<Alert> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfActionScreen = CursorUtil.getColumnIndexOrThrow(_cursor, "actionScreen");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsDismissed = CursorUtil.getColumnIndexOrThrow(_cursor, "isDismissed");
          final List<Alert> _result = new ArrayList<Alert>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Alert _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpMessage;
            _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            final String _tmpActionScreen;
            _tmpActionScreen = _cursor.getString(_cursorIndexOfActionScreen);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpIsDismissed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDismissed);
            _tmpIsDismissed = _tmp != 0;
            _item = new Alert(_tmpId,_tmpType,_tmpMessage,_tmpActionScreen,_tmpCreatedAt,_tmpIsDismissed);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
