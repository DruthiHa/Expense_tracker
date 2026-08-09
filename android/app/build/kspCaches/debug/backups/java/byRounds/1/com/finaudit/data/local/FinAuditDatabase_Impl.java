package com.finaudit.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.finaudit.data.local.dao.AlertDao;
import com.finaudit.data.local.dao.AlertDao_Impl;
import com.finaudit.data.local.dao.BudgetDao;
import com.finaudit.data.local.dao.BudgetDao_Impl;
import com.finaudit.data.local.dao.MerchantMappingDao;
import com.finaudit.data.local.dao.MerchantMappingDao_Impl;
import com.finaudit.data.local.dao.SavingsGoalDao;
import com.finaudit.data.local.dao.SavingsGoalDao_Impl;
import com.finaudit.data.local.dao.SubscriptionDao;
import com.finaudit.data.local.dao.SubscriptionDao_Impl;
import com.finaudit.data.local.dao.TransactionDao;
import com.finaudit.data.local.dao.TransactionDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FinAuditDatabase_Impl extends FinAuditDatabase {
  private volatile TransactionDao _transactionDao;

  private volatile SubscriptionDao _subscriptionDao;

  private volatile BudgetDao _budgetDao;

  private volatile SavingsGoalDao _savingsGoalDao;

  private volatile AlertDao _alertDao;

  private volatile MerchantMappingDao _merchantMappingDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `amount` REAL NOT NULL, `direction` TEXT NOT NULL, `merchantName` TEXT NOT NULL, `merchantNormalized` TEXT NOT NULL, `category` TEXT NOT NULL, `categoryConfidence` REAL NOT NULL, `paymentMethod` TEXT NOT NULL, `accountLast4` TEXT NOT NULL, `sourceType` TEXT NOT NULL, `rawMessageHash` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `notes` TEXT NOT NULL, `isReviewed` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `subscriptions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `emoji` TEXT NOT NULL, `amount` REAL NOT NULL, `billingCycle` TEXT NOT NULL, `category` TEXT NOT NULL, `renewDate` INTEGER NOT NULL, `lastTransactionDate` INTEGER NOT NULL, `status` TEXT NOT NULL, `notes` TEXT NOT NULL, `autoDetected` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `budgets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `category` TEXT NOT NULL, `monthlyLimit` REAL NOT NULL, `month` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `savings_goals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `emoji` TEXT NOT NULL, `targetAmount` REAL NOT NULL, `savedAmount` REAL NOT NULL, `deadline` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `alerts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `type` TEXT NOT NULL, `message` TEXT NOT NULL, `actionScreen` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `isDismissed` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `merchant_mappings` (`merchantNormalized` TEXT NOT NULL, `category` TEXT NOT NULL, `source` TEXT NOT NULL, `count` INTEGER NOT NULL, PRIMARY KEY(`merchantNormalized`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'b2cc9b3379700adafb1686d7d703273c')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `transactions`");
        db.execSQL("DROP TABLE IF EXISTS `subscriptions`");
        db.execSQL("DROP TABLE IF EXISTS `budgets`");
        db.execSQL("DROP TABLE IF EXISTS `savings_goals`");
        db.execSQL("DROP TABLE IF EXISTS `alerts`");
        db.execSQL("DROP TABLE IF EXISTS `merchant_mappings`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsTransactions = new HashMap<String, TableInfo.Column>(14);
        _columnsTransactions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("direction", new TableInfo.Column("direction", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("merchantName", new TableInfo.Column("merchantName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("merchantNormalized", new TableInfo.Column("merchantNormalized", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("categoryConfidence", new TableInfo.Column("categoryConfidence", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("paymentMethod", new TableInfo.Column("paymentMethod", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("accountLast4", new TableInfo.Column("accountLast4", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("sourceType", new TableInfo.Column("sourceType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("rawMessageHash", new TableInfo.Column("rawMessageHash", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("isReviewed", new TableInfo.Column("isReviewed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTransactions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTransactions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTransactions = new TableInfo("transactions", _columnsTransactions, _foreignKeysTransactions, _indicesTransactions);
        final TableInfo _existingTransactions = TableInfo.read(db, "transactions");
        if (!_infoTransactions.equals(_existingTransactions)) {
          return new RoomOpenHelper.ValidationResult(false, "transactions(com.finaudit.domain.model.Transaction).\n"
                  + " Expected:\n" + _infoTransactions + "\n"
                  + " Found:\n" + _existingTransactions);
        }
        final HashMap<String, TableInfo.Column> _columnsSubscriptions = new HashMap<String, TableInfo.Column>(11);
        _columnsSubscriptions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubscriptions.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubscriptions.put("emoji", new TableInfo.Column("emoji", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubscriptions.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubscriptions.put("billingCycle", new TableInfo.Column("billingCycle", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubscriptions.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubscriptions.put("renewDate", new TableInfo.Column("renewDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubscriptions.put("lastTransactionDate", new TableInfo.Column("lastTransactionDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubscriptions.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubscriptions.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubscriptions.put("autoDetected", new TableInfo.Column("autoDetected", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSubscriptions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSubscriptions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSubscriptions = new TableInfo("subscriptions", _columnsSubscriptions, _foreignKeysSubscriptions, _indicesSubscriptions);
        final TableInfo _existingSubscriptions = TableInfo.read(db, "subscriptions");
        if (!_infoSubscriptions.equals(_existingSubscriptions)) {
          return new RoomOpenHelper.ValidationResult(false, "subscriptions(com.finaudit.domain.model.Subscription).\n"
                  + " Expected:\n" + _infoSubscriptions + "\n"
                  + " Found:\n" + _existingSubscriptions);
        }
        final HashMap<String, TableInfo.Column> _columnsBudgets = new HashMap<String, TableInfo.Column>(4);
        _columnsBudgets.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("monthlyLimit", new TableInfo.Column("monthlyLimit", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("month", new TableInfo.Column("month", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBudgets = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBudgets = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBudgets = new TableInfo("budgets", _columnsBudgets, _foreignKeysBudgets, _indicesBudgets);
        final TableInfo _existingBudgets = TableInfo.read(db, "budgets");
        if (!_infoBudgets.equals(_existingBudgets)) {
          return new RoomOpenHelper.ValidationResult(false, "budgets(com.finaudit.domain.model.Budget).\n"
                  + " Expected:\n" + _infoBudgets + "\n"
                  + " Found:\n" + _existingBudgets);
        }
        final HashMap<String, TableInfo.Column> _columnsSavingsGoals = new HashMap<String, TableInfo.Column>(6);
        _columnsSavingsGoals.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavingsGoals.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavingsGoals.put("emoji", new TableInfo.Column("emoji", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavingsGoals.put("targetAmount", new TableInfo.Column("targetAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavingsGoals.put("savedAmount", new TableInfo.Column("savedAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavingsGoals.put("deadline", new TableInfo.Column("deadline", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSavingsGoals = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSavingsGoals = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSavingsGoals = new TableInfo("savings_goals", _columnsSavingsGoals, _foreignKeysSavingsGoals, _indicesSavingsGoals);
        final TableInfo _existingSavingsGoals = TableInfo.read(db, "savings_goals");
        if (!_infoSavingsGoals.equals(_existingSavingsGoals)) {
          return new RoomOpenHelper.ValidationResult(false, "savings_goals(com.finaudit.domain.model.SavingsGoal).\n"
                  + " Expected:\n" + _infoSavingsGoals + "\n"
                  + " Found:\n" + _existingSavingsGoals);
        }
        final HashMap<String, TableInfo.Column> _columnsAlerts = new HashMap<String, TableInfo.Column>(6);
        _columnsAlerts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlerts.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlerts.put("message", new TableInfo.Column("message", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlerts.put("actionScreen", new TableInfo.Column("actionScreen", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlerts.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlerts.put("isDismissed", new TableInfo.Column("isDismissed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAlerts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAlerts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAlerts = new TableInfo("alerts", _columnsAlerts, _foreignKeysAlerts, _indicesAlerts);
        final TableInfo _existingAlerts = TableInfo.read(db, "alerts");
        if (!_infoAlerts.equals(_existingAlerts)) {
          return new RoomOpenHelper.ValidationResult(false, "alerts(com.finaudit.domain.model.Alert).\n"
                  + " Expected:\n" + _infoAlerts + "\n"
                  + " Found:\n" + _existingAlerts);
        }
        final HashMap<String, TableInfo.Column> _columnsMerchantMappings = new HashMap<String, TableInfo.Column>(4);
        _columnsMerchantMappings.put("merchantNormalized", new TableInfo.Column("merchantNormalized", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMerchantMappings.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMerchantMappings.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMerchantMappings.put("count", new TableInfo.Column("count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMerchantMappings = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMerchantMappings = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMerchantMappings = new TableInfo("merchant_mappings", _columnsMerchantMappings, _foreignKeysMerchantMappings, _indicesMerchantMappings);
        final TableInfo _existingMerchantMappings = TableInfo.read(db, "merchant_mappings");
        if (!_infoMerchantMappings.equals(_existingMerchantMappings)) {
          return new RoomOpenHelper.ValidationResult(false, "merchant_mappings(com.finaudit.domain.model.MerchantMapping).\n"
                  + " Expected:\n" + _infoMerchantMappings + "\n"
                  + " Found:\n" + _existingMerchantMappings);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "b2cc9b3379700adafb1686d7d703273c", "2e4c50fc41972212eacfb178778043c0");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "transactions","subscriptions","budgets","savings_goals","alerts","merchant_mappings");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `transactions`");
      _db.execSQL("DELETE FROM `subscriptions`");
      _db.execSQL("DELETE FROM `budgets`");
      _db.execSQL("DELETE FROM `savings_goals`");
      _db.execSQL("DELETE FROM `alerts`");
      _db.execSQL("DELETE FROM `merchant_mappings`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(TransactionDao.class, TransactionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SubscriptionDao.class, SubscriptionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BudgetDao.class, BudgetDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SavingsGoalDao.class, SavingsGoalDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AlertDao.class, AlertDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MerchantMappingDao.class, MerchantMappingDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public TransactionDao transactionDao() {
    if (_transactionDao != null) {
      return _transactionDao;
    } else {
      synchronized(this) {
        if(_transactionDao == null) {
          _transactionDao = new TransactionDao_Impl(this);
        }
        return _transactionDao;
      }
    }
  }

  @Override
  public SubscriptionDao subscriptionDao() {
    if (_subscriptionDao != null) {
      return _subscriptionDao;
    } else {
      synchronized(this) {
        if(_subscriptionDao == null) {
          _subscriptionDao = new SubscriptionDao_Impl(this);
        }
        return _subscriptionDao;
      }
    }
  }

  @Override
  public BudgetDao budgetDao() {
    if (_budgetDao != null) {
      return _budgetDao;
    } else {
      synchronized(this) {
        if(_budgetDao == null) {
          _budgetDao = new BudgetDao_Impl(this);
        }
        return _budgetDao;
      }
    }
  }

  @Override
  public SavingsGoalDao savingsGoalDao() {
    if (_savingsGoalDao != null) {
      return _savingsGoalDao;
    } else {
      synchronized(this) {
        if(_savingsGoalDao == null) {
          _savingsGoalDao = new SavingsGoalDao_Impl(this);
        }
        return _savingsGoalDao;
      }
    }
  }

  @Override
  public AlertDao alertDao() {
    if (_alertDao != null) {
      return _alertDao;
    } else {
      synchronized(this) {
        if(_alertDao == null) {
          _alertDao = new AlertDao_Impl(this);
        }
        return _alertDao;
      }
    }
  }

  @Override
  public MerchantMappingDao merchantMappingDao() {
    if (_merchantMappingDao != null) {
      return _merchantMappingDao;
    } else {
      synchronized(this) {
        if(_merchantMappingDao == null) {
          _merchantMappingDao = new MerchantMappingDao_Impl(this);
        }
        return _merchantMappingDao;
      }
    }
  }
}
