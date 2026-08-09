package com.finaudit.di;

import com.finaudit.data.local.FinAuditDatabase;
import com.finaudit.domain.repository.FinAuditRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideRepositoryFactory implements Factory<FinAuditRepository> {
  private final Provider<FinAuditDatabase> dbProvider;

  public AppModule_ProvideRepositoryFactory(Provider<FinAuditDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public FinAuditRepository get() {
    return provideRepository(dbProvider.get());
  }

  public static AppModule_ProvideRepositoryFactory create(Provider<FinAuditDatabase> dbProvider) {
    return new AppModule_ProvideRepositoryFactory(dbProvider);
  }

  public static FinAuditRepository provideRepository(FinAuditDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRepository(db));
  }
}
