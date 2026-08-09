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
import com.finaudit.domain.model.Transaction;
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
public final class TransactionDao_Impl implements TransactionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Transaction> __insertionAdapterOfTransaction;

  private final EntityDeletionOrUpdateAdapter<Transaction> __deletionAdapterOfTransaction;

  private final EntityDeletionOrUpdateAdapter<Transaction> __updateAdapterOfTransaction;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public TransactionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTransaction = new EntityInsertionAdapter<Transaction>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `transactions` (`id`,`amount`,`direction`,`merchantName`,`merchantNormalized`,`category`,`categoryConfidence`,`paymentMethod`,`accountLast4`,`sourceType`,`rawMessageHash`,`timestamp`,`notes`,`isReviewed`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Transaction entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getAmount());
        statement.bindString(3, entity.getDirection());
        statement.bindString(4, entity.getMerchantName());
        statement.bindString(5, entity.getMerchantNormalized());
        statement.bindString(6, entity.getCategory());
        statement.bindDouble(7, entity.getCategoryConfidence());
        statement.bindString(8, entity.getPaymentMethod());
        statement.bindString(9, entity.getAccountLast4());
        statement.bindString(10, entity.getSourceType());
        statement.bindString(11, entity.getRawMessageHash());
        statement.bindLong(12, entity.getTimestamp());
        statement.bindString(13, entity.getNotes());
        final int _tmp = entity.isReviewed() ? 1 : 0;
        statement.bindLong(14, _tmp);
      }
    };
    this.__deletionAdapterOfTransaction = new EntityDeletionOrUpdateAdapter<Transaction>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `transactions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Transaction entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfTransaction = new EntityDeletionOrUpdateAdapter<Transaction>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `transactions` SET `id` = ?,`amount` = ?,`direction` = ?,`merchantName` = ?,`merchantNormalized` = ?,`category` = ?,`categoryConfidence` = ?,`paymentMethod` = ?,`accountLast4` = ?,`sourceType` = ?,`rawMessageHash` = ?,`timestamp` = ?,`notes` = ?,`isReviewed` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Transaction entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getAmount());
        statement.bindString(3, entity.getDirection());
        statement.bindString(4, entity.getMerchantName());
        statement.bindString(5, entity.getMerchantNormalized());
        statement.bindString(6, entity.getCategory());
        statement.bindDouble(7, entity.getCategoryConfidence());
        statement.bindString(8, entity.getPaymentMethod());
        statement.bindString(9, entity.getAccountLast4());
        statement.bindString(10, entity.getSourceType());
        statement.bindString(11, entity.getRawMessageHash());
        statement.bindLong(12, entity.getTimestamp());
        statement.bindString(13, entity.getNotes());
        final int _tmp = entity.isReviewed() ? 1 : 0;
        statement.bindLong(14, _tmp);
        statement.bindLong(15, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM transactions";
        return _query;
      }
    };
  }

  @Override
  public Object insertTransaction(final Transaction transaction,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTransaction.insertAndReturnId(transaction);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTransaction(final Transaction transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTransaction.handle(transaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTransaction(final Transaction transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTransaction.handle(transaction);
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
  public Flow<List<Transaction>> getAllTransactionsFlow() {
    final String _sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<Transaction>>() {
      @Override
      @NonNull
      public List<Transaction> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfMerchantName = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantName");
          final int _cursorIndexOfMerchantNormalized = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantNormalized");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfCategoryConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryConfidence");
          final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMethod");
          final int _cursorIndexOfAccountLast4 = CursorUtil.getColumnIndexOrThrow(_cursor, "accountLast4");
          final int _cursorIndexOfSourceType = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceType");
          final int _cursorIndexOfRawMessageHash = CursorUtil.getColumnIndexOrThrow(_cursor, "rawMessageHash");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIsReviewed = CursorUtil.getColumnIndexOrThrow(_cursor, "isReviewed");
          final List<Transaction> _result = new ArrayList<Transaction>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Transaction _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpDirection;
            _tmpDirection = _cursor.getString(_cursorIndexOfDirection);
            final String _tmpMerchantName;
            _tmpMerchantName = _cursor.getString(_cursorIndexOfMerchantName);
            final String _tmpMerchantNormalized;
            _tmpMerchantNormalized = _cursor.getString(_cursorIndexOfMerchantNormalized);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final float _tmpCategoryConfidence;
            _tmpCategoryConfidence = _cursor.getFloat(_cursorIndexOfCategoryConfidence);
            final String _tmpPaymentMethod;
            _tmpPaymentMethod = _cursor.getString(_cursorIndexOfPaymentMethod);
            final String _tmpAccountLast4;
            _tmpAccountLast4 = _cursor.getString(_cursorIndexOfAccountLast4);
            final String _tmpSourceType;
            _tmpSourceType = _cursor.getString(_cursorIndexOfSourceType);
            final String _tmpRawMessageHash;
            _tmpRawMessageHash = _cursor.getString(_cursorIndexOfRawMessageHash);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final boolean _tmpIsReviewed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReviewed);
            _tmpIsReviewed = _tmp != 0;
            _item = new Transaction(_tmpId,_tmpAmount,_tmpDirection,_tmpMerchantName,_tmpMerchantNormalized,_tmpCategory,_tmpCategoryConfidence,_tmpPaymentMethod,_tmpAccountLast4,_tmpSourceType,_tmpRawMessageHash,_tmpTimestamp,_tmpNotes,_tmpIsReviewed);
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
  public Object getAllTransactions(final Continuation<? super List<Transaction>> $completion) {
    final String _sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Transaction>>() {
      @Override
      @NonNull
      public List<Transaction> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfMerchantName = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantName");
          final int _cursorIndexOfMerchantNormalized = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantNormalized");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfCategoryConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryConfidence");
          final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMethod");
          final int _cursorIndexOfAccountLast4 = CursorUtil.getColumnIndexOrThrow(_cursor, "accountLast4");
          final int _cursorIndexOfSourceType = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceType");
          final int _cursorIndexOfRawMessageHash = CursorUtil.getColumnIndexOrThrow(_cursor, "rawMessageHash");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIsReviewed = CursorUtil.getColumnIndexOrThrow(_cursor, "isReviewed");
          final List<Transaction> _result = new ArrayList<Transaction>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Transaction _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpDirection;
            _tmpDirection = _cursor.getString(_cursorIndexOfDirection);
            final String _tmpMerchantName;
            _tmpMerchantName = _cursor.getString(_cursorIndexOfMerchantName);
            final String _tmpMerchantNormalized;
            _tmpMerchantNormalized = _cursor.getString(_cursorIndexOfMerchantNormalized);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final float _tmpCategoryConfidence;
            _tmpCategoryConfidence = _cursor.getFloat(_cursorIndexOfCategoryConfidence);
            final String _tmpPaymentMethod;
            _tmpPaymentMethod = _cursor.getString(_cursorIndexOfPaymentMethod);
            final String _tmpAccountLast4;
            _tmpAccountLast4 = _cursor.getString(_cursorIndexOfAccountLast4);
            final String _tmpSourceType;
            _tmpSourceType = _cursor.getString(_cursorIndexOfSourceType);
            final String _tmpRawMessageHash;
            _tmpRawMessageHash = _cursor.getString(_cursorIndexOfRawMessageHash);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final boolean _tmpIsReviewed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReviewed);
            _tmpIsReviewed = _tmp != 0;
            _item = new Transaction(_tmpId,_tmpAmount,_tmpDirection,_tmpMerchantName,_tmpMerchantNormalized,_tmpCategory,_tmpCategoryConfidence,_tmpPaymentMethod,_tmpAccountLast4,_tmpSourceType,_tmpRawMessageHash,_tmpTimestamp,_tmpNotes,_tmpIsReviewed);
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

  @Override
  public Flow<List<Transaction>> getPendingReviewTransactionsFlow() {
    final String _sql = "SELECT * FROM transactions WHERE isReviewed = 0 ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<Transaction>>() {
      @Override
      @NonNull
      public List<Transaction> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfMerchantName = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantName");
          final int _cursorIndexOfMerchantNormalized = CursorUtil.getColumnIndexOrThrow(_cursor, "merchantNormalized");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfCategoryConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryConfidence");
          final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMethod");
          final int _cursorIndexOfAccountLast4 = CursorUtil.getColumnIndexOrThrow(_cursor, "accountLast4");
          final int _cursorIndexOfSourceType = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceType");
          final int _cursorIndexOfRawMessageHash = CursorUtil.getColumnIndexOrThrow(_cursor, "rawMessageHash");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIsReviewed = CursorUtil.getColumnIndexOrThrow(_cursor, "isReviewed");
          final List<Transaction> _result = new ArrayList<Transaction>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Transaction _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpDirection;
            _tmpDirection = _cursor.getString(_cursorIndexOfDirection);
            final String _tmpMerchantName;
            _tmpMerchantName = _cursor.getString(_cursorIndexOfMerchantName);
            final String _tmpMerchantNormalized;
            _tmpMerchantNormalized = _cursor.getString(_cursorIndexOfMerchantNormalized);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final float _tmpCategoryConfidence;
            _tmpCategoryConfidence = _cursor.getFloat(_cursorIndexOfCategoryConfidence);
            final String _tmpPaymentMethod;
            _tmpPaymentMethod = _cursor.getString(_cursorIndexOfPaymentMethod);
            final String _tmpAccountLast4;
            _tmpAccountLast4 = _cursor.getString(_cursorIndexOfAccountLast4);
            final String _tmpSourceType;
            _tmpSourceType = _cursor.getString(_cursorIndexOfSourceType);
            final String _tmpRawMessageHash;
            _tmpRawMessageHash = _cursor.getString(_cursorIndexOfRawMessageHash);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final boolean _tmpIsReviewed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReviewed);
            _tmpIsReviewed = _tmp != 0;
            _item = new Transaction(_tmpId,_tmpAmount,_tmpDirection,_tmpMerchantName,_tmpMerchantNormalized,_tmpCategory,_tmpCategoryConfidence,_tmpPaymentMethod,_tmpAccountLast4,_tmpSourceType,_tmpRawMessageHash,_tmpTimestamp,_tmpNotes,_tmpIsReviewed);
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
