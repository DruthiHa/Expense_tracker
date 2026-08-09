package com.finaudit.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
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
import com.finaudit.domain.model.Subscription;
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
public final class SubscriptionDao_Impl implements SubscriptionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Subscription> __insertionAdapterOfSubscription;

  private final EntityDeletionOrUpdateAdapter<Subscription> __deletionAdapterOfSubscription;

  private final EntityDeletionOrUpdateAdapter<Subscription> __updateAdapterOfSubscription;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public SubscriptionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSubscription = new EntityInsertionAdapter<Subscription>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `subscriptions` (`id`,`name`,`emoji`,`amount`,`billingCycle`,`category`,`renewDate`,`lastTransactionDate`,`status`,`notes`,`autoDetected`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Subscription entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getEmoji());
        statement.bindDouble(4, entity.getAmount());
        statement.bindString(5, entity.getBillingCycle());
        statement.bindString(6, entity.getCategory());
        statement.bindLong(7, entity.getRenewDate());
        statement.bindLong(8, entity.getLastTransactionDate());
        statement.bindString(9, entity.getStatus());
        statement.bindString(10, entity.getNotes());
        final int _tmp = entity.getAutoDetected() ? 1 : 0;
        statement.bindLong(11, _tmp);
      }
    };
    this.__deletionAdapterOfSubscription = new EntityDeletionOrUpdateAdapter<Subscription>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `subscriptions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Subscription entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfSubscription = new EntityDeletionOrUpdateAdapter<Subscription>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `subscriptions` SET `id` = ?,`name` = ?,`emoji` = ?,`amount` = ?,`billingCycle` = ?,`category` = ?,`renewDate` = ?,`lastTransactionDate` = ?,`status` = ?,`notes` = ?,`autoDetected` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Subscription entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getEmoji());
        statement.bindDouble(4, entity.getAmount());
        statement.bindString(5, entity.getBillingCycle());
        statement.bindString(6, entity.getCategory());
        statement.bindLong(7, entity.getRenewDate());
        statement.bindLong(8, entity.getLastTransactionDate());
        statement.bindString(9, entity.getStatus());
        statement.bindString(10, entity.getNotes());
        final int _tmp = entity.getAutoDetected() ? 1 : 0;
        statement.bindLong(11, _tmp);
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM subscriptions";
        return _query;
      }
    };
  }

  @Override
  public Object insertSubscription(final Subscription subscription,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSubscription.insertAndReturnId(subscription);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSubscription(final Subscription subscription,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSubscription.handle(subscription);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSubscription(final Subscription subscription,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSubscription.handle(subscription);
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
  public Flow<List<Subscription>> getAllSubscriptionsFlow() {
    final String _sql = "SELECT * FROM subscriptions ORDER BY renewDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"subscriptions"}, new Callable<List<Subscription>>() {
      @Override
      @NonNull
      public List<Subscription> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmoji = CursorUtil.getColumnIndexOrThrow(_cursor, "emoji");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfBillingCycle = CursorUtil.getColumnIndexOrThrow(_cursor, "billingCycle");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfRenewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "renewDate");
          final int _cursorIndexOfLastTransactionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastTransactionDate");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfAutoDetected = CursorUtil.getColumnIndexOrThrow(_cursor, "autoDetected");
          final List<Subscription> _result = new ArrayList<Subscription>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Subscription _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpEmoji;
            _tmpEmoji = _cursor.getString(_cursorIndexOfEmoji);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpBillingCycle;
            _tmpBillingCycle = _cursor.getString(_cursorIndexOfBillingCycle);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final long _tmpRenewDate;
            _tmpRenewDate = _cursor.getLong(_cursorIndexOfRenewDate);
            final long _tmpLastTransactionDate;
            _tmpLastTransactionDate = _cursor.getLong(_cursorIndexOfLastTransactionDate);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final boolean _tmpAutoDetected;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAutoDetected);
            _tmpAutoDetected = _tmp != 0;
            _item = new Subscription(_tmpId,_tmpName,_tmpEmoji,_tmpAmount,_tmpBillingCycle,_tmpCategory,_tmpRenewDate,_tmpLastTransactionDate,_tmpStatus,_tmpNotes,_tmpAutoDetected);
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

  @Override
  public Object getAllSubscriptions(final Continuation<? super List<Subscription>> $completion) {
    final String _sql = "SELECT * FROM subscriptions ORDER BY renewDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Subscription>>() {
      @Override
      @NonNull
      public List<Subscription> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEmoji = CursorUtil.getColumnIndexOrThrow(_cursor, "emoji");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfBillingCycle = CursorUtil.getColumnIndexOrThrow(_cursor, "billingCycle");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfRenewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "renewDate");
          final int _cursorIndexOfLastTransactionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastTransactionDate");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfAutoDetected = CursorUtil.getColumnIndexOrThrow(_cursor, "autoDetected");
          final List<Subscription> _result = new ArrayList<Subscription>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Subscription _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpEmoji;
            _tmpEmoji = _cursor.getString(_cursorIndexOfEmoji);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpBillingCycle;
            _tmpBillingCycle = _cursor.getString(_cursorIndexOfBillingCycle);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final long _tmpRenewDate;
            _tmpRenewDate = _cursor.getLong(_cursorIndexOfRenewDate);
            final long _tmpLastTransactionDate;
            _tmpLastTransactionDate = _cursor.getLong(_cursorIndexOfLastTransactionDate);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final boolean _tmpAutoDetected;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAutoDetected);
            _tmpAutoDetected = _tmp != 0;
            _item = new Subscription(_tmpId,_tmpName,_tmpEmoji,_tmpAmount,_tmpBillingCycle,_tmpCategory,_tmpRenewDate,_tmpLastTransactionDate,_tmpStatus,_tmpNotes,_tmpAutoDetected);
            _result.add(_item);
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
