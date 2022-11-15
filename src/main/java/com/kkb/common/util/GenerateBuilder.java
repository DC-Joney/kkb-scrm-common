package com.kkb.common.util;

import cn.hutool.core.io.resource.ClassPathResource;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Example:
 * <pre>
 *     AutoGenerator generator = GenerateBuilder.builder()
 *                  .global()
 *                  //设置 作者
 *                  .author("zhangyang")
 *                  .and()
 *                  .packages()
 *
 *                  //设置包名称
 *                  .packageName("com.kkb.im.data")
 *                  .and()
 *                  .dataSource()
 *
 *                  //配置数据库url地址
 *                  .url("jdbc:mysql://192.168.100.54:3306/kkb-scrm-im-send?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&useAffectedRows=true")
 *                  //配置数据库userName
 *                  .userName("test")
 *
 *                  //配置数据库password
 *                  .password("Meihao100@bfbd")
 *
 *                  //配置数据库DriverClass
 *                  .driverName("com.mysql.jdbc.Driver")
 *                  .and()
 *                  .injection()
 *                  .addDefaultOutConfig()
 *                  .and()
 *                  .strategy()
 *
 *                  //添加表名称
 *                  .addTableName("friend_adopt_rule")
 *                  .addTableName("friend_apply_info")
 *                  .and()
 *
 *                  //添加模板引擎
 *                  .templateEngine(new FreemarkerTemplateEngine())
 *                  .build();
 *
 *  generator.execute();
 * </pre>
 *
 * 用于生成Mybatis 配置
 * @author zhangyang
 */
public class GenerateBuilder {

    final static Path generatePath;
    final static Path dirPath;

    private static final String GENERATE_PATH = "generate";

    private static final String PREFIX = "src/main/java";

    static {
        generatePath = Paths.get(".").resolveSibling(Paths.get(GENERATE_PATH)).toAbsolutePath();
        dirPath = Paths.get(PREFIX);
    }

    private final LinkedHashMap<Class<? extends ConfigBuilder>, ConfigBuilder> configInstance = new LinkedHashMap<>(8);

    private AbstractTemplateEngine templateEngine;

    public GlobalSetting global() {
        return getOrApply(new GlobalSetting());
    }

    public DataSourceSetting dataSource() {
        return getOrApply(new DataSourceSetting());
    }

    public PackageSetting packages() {
        return getOrApply(new PackageSetting());
    }

    public StrategySetting strategy() {
        return getOrApply(new StrategySetting());
    }

    public InjectionSetting injection() {
        return getOrApply(new InjectionSetting());
    }

    public static GenerateBuilder builder() {
        return new GenerateBuilder();
    }

    @SuppressWarnings("unchecked")
    private <C extends ConfigBuilder> C getOrApply(C configurer) {
        C configBuilder = (C) configInstance.getOrDefault(configurer.getClass(), null);

        if (configBuilder != null){
            configBuilder.setBuilder(this);
            return configBuilder;
        }

        configInstance.put(configurer.getClass(), configurer);
        configurer.setBuilder(this);
        return configurer;
    }

    public GenerateBuilder templateEngine(AbstractTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        return this;
    }


    public AutoGenerator build() {
        AutoGenerator generator = new AutoGenerator();
        configInstance.values()
                .forEach(builder -> builder.build(generator));
        return generator.setTemplateEngine(templateEngine);
    }


    public static abstract class ConfigBuilder {

        private GenerateBuilder builder;

        public void setBuilder(GenerateBuilder builder) {
            this.builder = builder;
        }

        public GenerateBuilder and() {
            return builder;
        }

        abstract void build(AutoGenerator autoGenerator);
    }


    @Setter
    @Accessors(fluent = true, chain = true)
    public static class GlobalSetting extends ConfigBuilder {
        /**
         * 作者名称
         */
        private String author;

        /**
         * 是否启动swagger 注解
         */
        private boolean swagger = true;

        /**
         * 是否自动生成baseResultMap
         */
        private boolean generateBaseResultMap = true;

        private boolean open = false;

        private IdType idType = IdType.ASSIGN_ID;

        private Path outputDir = generatePath.resolve(dirPath).toAbsolutePath();

        public void build(AutoGenerator autoGenerator) {
            GlobalConfig config = new GlobalConfig();
            config.setBaseResultMap(generateBaseResultMap)
                    .setSwagger2(swagger)
                    .setAuthor(author)
                    .setIdType(idType)
                    .setOpen(open)
                    .setOutputDir(outputDir.toString());

            autoGenerator.setGlobalConfig(config);
        }


    }


    @Setter
    @Accessors(chain = true, fluent = true)
    public static class DataSourceSetting extends ConfigBuilder {
        private String url;
        private String driverName;
        private String userName;
        private String password;
        private DbType dbType = DbType.MYSQL;

        public void build(AutoGenerator autoGenerator) {
            DataSourceConfig sourceConfig = new DataSourceConfig();
            sourceConfig.setDriverName(driverName)
                    .setPassword(password)
                    .setUsername(userName)
                    .setDbType(dbType)
                    .setUrl(url);

            autoGenerator.setDataSource(sourceConfig);
        }

    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class PackageSetting extends ConfigBuilder {
        String packageName;

        public void build(AutoGenerator generator) {
            PackageConfig packageConfig = new PackageConfig();
            packageConfig.setParent(packageName);
            generator.setPackageInfo(packageConfig);
        }
    }


    @Setter
    @Accessors(chain = true, fluent = true)
    public static class TemplateSetting extends ConfigBuilder {
        String xmlTemplate = ConstVal.TEMPLATE_XML;

        public void build(AutoGenerator generator) {
            TemplateConfig templateConfig = new TemplateConfig();
            templateConfig.setXml(xmlTemplate);
            generator.setTemplate(templateConfig);
        }
    }


    @Setter
    @Accessors(fluent = true, chain = true)
    public static class StrategySetting extends ConfigBuilder {
        /**
         * 命名策略
         */
        private NamingStrategy nameStrategy = NamingStrategy.underline_to_camel;

        private NamingStrategy columnNaming = NamingStrategy.underline_to_camel;

        private boolean entityLombok = true;

        private boolean restControllerStyle = true;

        private List<String> tableNames = new ArrayList<>();

        private boolean controllerMappingHyphenStyle = true;

        private String tablePrefix = "";

        public StrategySetting addTableName(String tableName) {
            tableNames.add(tableName);
            return this;
        }

        public void build(AutoGenerator autoGenerator) {
            StrategyConfig strategyConfig = new StrategyConfig();
            autoGenerator.setStrategy(strategyConfig);
            strategyConfig.setNaming(nameStrategy)
                    .setColumnNaming(columnNaming)
                    .setControllerMappingHyphenStyle(controllerMappingHyphenStyle)
                    .setEntityLombokModel(entityLombok)
                    .setTablePrefix(tablePrefix)
                    .setRestControllerStyle(restControllerStyle)
                    .setInclude(tableNames.toArray(new String[0]));

        }
    }


    @Setter
    @Accessors(fluent = true, chain = true)
    public static class InjectionSetting extends ConfigBuilder {

        private static final String DEFAULT_TEMPLATE_PATH = "/templates/mapper.xml.ftl";

        private List<FileOutConfig> configs = new ArrayList<>();

        private Supplier<InjectionConfig> configSupplier;

        private static final Function<String, FileOutConfig> configFunction = template -> new FileOutConfig(template) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return generatePath.toString() + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        };

        private static final Supplier<InjectionConfig> DEFAULT_INJECTION_SUPPLIER = () -> new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        public InjectionSetting addFileOutConfig(FileOutConfig outConfig) {
            configs.add(outConfig);
            return this;
        }


        public InjectionSetting addDefaultOutConfig() {
            configs.add(configFunction.apply(DEFAULT_TEMPLATE_PATH));
            return this;
        }

        public InjectionSetting addDefaultOutConfig(String templateName) {
            configs.add(configFunction.apply(templateName));
            return this;
        }


        @Override
        void build(AutoGenerator autoGenerator) {
            InjectionConfig injectionConfig = Optional.ofNullable(configSupplier)
                    .map(Supplier::get)
                    .orElseGet(DEFAULT_INJECTION_SUPPLIER);

            if (configs.size() <= 0)
                this.addDefaultOutConfig();

            injectionConfig.setFileOutConfigList(configs);
            autoGenerator.setCfg(injectionConfig);
        }
    }
}
