package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionOperations;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    public  DatabaseServiceCredentials getCredentials(@Value ("${VCAP_SERVICES}") String vcapServices){
        return new DatabaseServiceCredentials(vcapServices);
    }

    @Bean(name = "albumsDataSource")
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(serviceCredentials.jdbcUrl("albums-mysql"));
        return dataSource;
    }

    @Bean(name = "moviesDataSource")
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(serviceCredentials.jdbcUrl("movies-mysql"));
        return dataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter jpaVendorAdaptor(){
        HibernateJpaVendorAdapter jpa = new HibernateJpaVendorAdapter();
        jpa.setDatabase(Database.MYSQL);
        jpa.setGenerateDdl(true);
        jpa.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        return jpa;
    }

    @Bean(name = "albumsDataSourceEntity")
    public LocalContainerEntityManagerFactoryBean albumsDataSourceEntity(@Qualifier("albumsDataSource") DataSource dataSource, HibernateJpaVendorAdapter jpa) {
        LocalContainerEntityManagerFactoryBean localAlbumsEntity = new LocalContainerEntityManagerFactoryBean();
        localAlbumsEntity.setDataSource(dataSource);
        localAlbumsEntity.setJpaVendorAdapter(jpa);
        localAlbumsEntity.setPackagesToScan(new String[] {"org.superbiz.moviefun.albums"});
        localAlbumsEntity.setPersistenceUnitName("album-unit");
        return localAlbumsEntity;
    }

    @Bean(name = "moviesDataSourceEntity")
    public LocalContainerEntityManagerFactoryBean moviesDataSourceEntity(@Qualifier("moviesDataSource") DataSource dataSource, HibernateJpaVendorAdapter jpa) {
        LocalContainerEntityManagerFactoryBean localMoviesEntity = new LocalContainerEntityManagerFactoryBean();
        localMoviesEntity.setDataSource(dataSource);
        localMoviesEntity.setJpaVendorAdapter(jpa);
        localMoviesEntity.setPackagesToScan(new String[] {"org.superbiz.moviefun.movies"});
        localMoviesEntity.setPersistenceUnitName("movie-unit");
        return localMoviesEntity;
    }

    @Bean(name = "platformTransactionManagerAlbums")
    public PlatformTransactionManager platformTransactionManagerAlbums(@Qualifier("albumsDataSourceEntity") EntityManagerFactory albumsDataSourceEntity){
        JpaTransactionManager albumTransactionManager = new JpaTransactionManager();
        albumTransactionManager.setEntityManagerFactory(albumsDataSourceEntity);
        return albumTransactionManager;
    }
    @Bean(name = "platformTransactionManagerMovies")
    public PlatformTransactionManager platformTransactionManagerMovies(@Qualifier("moviesDataSourceEntity") EntityManagerFactory moviesDataSourceEntity){
        JpaTransactionManager moviesTransactionManager = new JpaTransactionManager();
        moviesTransactionManager.setEntityManagerFactory(moviesDataSourceEntity);
        return moviesTransactionManager;
    }


    @Bean(name="albumsTransactionOperations")
    public TransactionOperations AlbumstransactionOperations
            (@Qualifier("platformTransactionManagerAlbums")PlatformTransactionManager platformTransactionManager) {

        return new TransactionTemplate(platformTransactionManager);

    }

    @Bean(name="moviesTransactionOperations")
    public TransactionOperations MoviestransactionOperations
            (@Qualifier("platformTransactionManagerMovies")PlatformTransactionManager platformTransactionManager) {
        return new TransactionTemplate(platformTransactionManager);
    }

}
