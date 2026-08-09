package com.finaudit.data.repository;

import com.finaudit.data.local.FinAuditDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class FinAuditRepositoryImpl_Factory implements Factory<FinAuditRepositoryImpl> {
  private final Provider<FinAuditDatabase> dbProvider;

  public FinAuditRepositoryImpl_Factory(Provider<FinAuditDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public FinAuditRepositoryImpl get() {
    return newInstance(dbProvider.get());
  }

  public static FinAuditRepositoryImpl_Factory create(Provider<FinAuditDatabase> dbProvider) {
    return new FinAuditRepositoryImpl_Factory(dbProvider);
  }

  public static FinAuditRepositoryImpl newInstance(FinAuditDatabase db) {
    return new FinAuditRepositoryImpl(db);
  }
}
