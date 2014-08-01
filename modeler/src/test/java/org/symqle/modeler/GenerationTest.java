package org.symqle.modeler;

import org.apache.commons.io.IOUtils;
import org.symqle.modeler.generator.FreeMarkerClassWriter;
import org.symqle.modeler.generator.GeneratedKeyWriter;
import org.symqle.modeler.generator.RegularClassWriter;
import org.symqle.modeler.generator.SaverWriter;
import org.symqle.modeler.metadata.ColumnTransformer;
import org.symqle.modeler.metadata.ForeignKeyTransformer;
import org.symqle.modeler.metadata.GeneratedFkTransformer;
import org.symqle.modeler.metadata.MetadataReader;
import org.symqle.modeler.metadata.TableSieve;
import org.symqle.modeler.metadata.TableTransformer;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.transformer.Filter;
import org.symqle.modeler.transformer.RegexpFilter;
import org.symqle.modeler.transformer.Transformer;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lvovich
 */
public class GenerationTest extends DatabaseTestBase {

    @Override
    public void setUp() throws Exception {
        System.err.println("Starting " + getName());
        packageDir.mkdirs();
        for (File file: packageDir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }

    @Override
    protected void tearDown() throws Exception {
        System.err.println("Done " + getName());
    }

    // generated code is different due to different data types in model
    // cannont use same expected code for all DBs, run for Apache Derby only
    @Override
    protected void runTest() throws Throwable {
        if ("Apache Derby".equals(getDatabaseName())) {
            super.runTest();
        }
    }

    private ColumnTransformer makeColumnTransformer(boolean naturalKeys) {
        final ColumnTransformer columnTransformer = new ColumnTransformer();
        columnTransformer.setNaturalKeys(naturalKeys);
        return columnTransformer;
    }
    private final List<Transformer> naturalKeyTransformers = Arrays.<Transformer>asList(createSieve(), new TableTransformer(),
            makeColumnTransformer(true), new GeneratedFkTransformer(), new ForeignKeyTransformer());

    private final List<Transformer> generatedKeyTransformers = Arrays.<Transformer>asList(createSieve(), new TableTransformer(),
            makeColumnTransformer(false), new GeneratedFkTransformer(), new ForeignKeyTransformer());

    private TableSieve createSieve() {
        final TableSieve sieve = new TableSieve();
        final RegexpFilter regexpFilter = new RegexpFilter();
        regexpFilter.setPattern("SYS.*");
        regexpFilter.setProperty("TABLE_NAME");
        sieve.setTableFilters(Arrays.<Filter>asList(regexpFilter));
        return sieve;
    }

    public void testTableNaturalKeys() throws Exception {
        generate(naturalKeyTransformers, "freemarker/Table.ftl", "", new RegularClassWriter());
        assertMatchesExpected("expected/natural", "AllTypes");
        assertMatchesExpected("expected/natural", "Department");
        assertMatchesExpected("expected/natural", "Detail");
        assertMatchesExpected("expected/natural", "Plain");
        assertMatchesExpected("expected/natural", "Plain1");
        assertMatchesExpected("expected/natural", "Second1");
    }

    public void testTableGeneratedKeys() throws Exception {
        generate(generatedKeyTransformers, "freemarker/Table.ftl", "", new RegularClassWriter());
        assertMatchesExpected("expected/generated", "AllTypes");
        assertMatchesExpected("expected/generated", "Department");
        assertMatchesExpected("expected/generated", "Detail");
        assertMatchesExpected("expected/generated", "Plain");
        assertMatchesExpected("expected/generated", "Second2");
        assertMatchesExpected("expected/generated", "First2");
    }

    public void testNoGeneratedIds() throws Exception {
        generate(naturalKeyTransformers, "freemarker/GeneratedKey.ftl", "Id", new GeneratedKeyWriter());
        assertEquals(0, packageDir.listFiles().length);
    }

    public void testGeneratedKeys() throws Exception {
        generate(generatedKeyTransformers, "freemarker/GeneratedKey.ftl", "Id", new GeneratedKeyWriter());
        assertFalse("MasterId", new File(packageDir, "MasterId.java").exists());
        assertFalse("DetailId", new File(packageDir, "DetailId.java").exists());
        assertFalse("AllTypesId", new File(packageDir, "AllTypesId.java").exists());
        assertFalse("PlainId", new File(packageDir, "PlainId.java").exists());
        assertFalse("Second1Id", new File(packageDir, "Second1Id.java").exists());
        assertFalse("First2Id", new File(packageDir, "First2Id.java").exists());
        assertFalse("Second2Id", new File(packageDir, "Second2Id.java").exists());
        assertMatchesExpected("expected/generated", "DepartmentId");
        assertMatchesExpected("expected/generated", "EmployeeId");
        assertMatchesExpected("expected/generated", "First1Id");
    }

    public void testDtoNaturalKeys() throws Exception {
        generate(naturalKeyTransformers, "freemarker/Dto.ftl", "Dto", new RegularClassWriter());
        assertMatchesExpected("expected/natural", "AllTypesDto");
        assertMatchesExpected("expected/natural", "DepartmentDto");
        assertMatchesExpected("expected/natural", "MasterDto");
        assertMatchesExpected("expected/natural", "DetailDto");
        assertMatchesExpected("expected/natural", "PlainDto");
    }

    public void testDtoGeneratedKeys() throws Exception {
        generate(generatedKeyTransformers, "freemarker/Dto.ftl", "Dto", new RegularClassWriter());
        assertMatchesExpected("expected/generated", "AllTypesDto");
        assertMatchesExpected("expected/generated", "DepartmentDto");
        assertMatchesExpected("expected/generated", "MasterDto");
        assertMatchesExpected("expected/generated", "DetailDto");
        assertMatchesExpected("expected/generated", "Second1Dto");
        assertMatchesExpected("expected/generated", "Second2Dto");
        assertMatchesExpected("expected/generated", "First2Dto");
    }

    public void testSelectorNaturalKeys() throws Exception {
        generate(naturalKeyTransformers, "freemarker/Selector.ftl", "Selector", new RegularClassWriter());
        assertMatchesExpected("expected/natural", "AllTypesSelector");
        assertMatchesExpected("expected/natural", "DepartmentSelector");
        assertMatchesExpected("expected/natural", "MasterSelector");
        assertMatchesExpected("expected/natural", "DetailSelector");
        assertMatchesExpected("expected/natural", "PlainSelector");
    }

    public void testSelectorGeneratedKeys() throws Exception {
        generate(generatedKeyTransformers, "freemarker/Selector.ftl", "Selector", new RegularClassWriter());
        assertMatchesExpected("expected/generated", "AllTypesSelector");
        assertMatchesExpected("expected/generated", "DepartmentSelector");
        assertMatchesExpected("expected/generated", "MasterSelector");
        assertMatchesExpected("expected/generated", "DetailSelector");
        assertMatchesExpected("expected/generated", "Second1Selector");
    }

    public void testSmartSelectorNaturalKeys() throws Exception {
        generate(naturalKeyTransformers, "freemarker/SmartSelector.ftl", "SmartSelector", new RegularClassWriter());
        assertMatchesExpected("expected/natural", "AllTypesSmartSelector");
        assertMatchesExpected("expected/natural", "DepartmentSmartSelector");
        assertMatchesExpected("expected/natural", "MasterSmartSelector");
        assertMatchesExpected("expected/natural", "DetailSmartSelector");
        assertMatchesExpected("expected/natural", "PlainSmartSelector");
    }

    public void testSmartSelectorGeneratedKeys() throws Exception {
        generate(generatedKeyTransformers, "freemarker/SmartSelector.ftl", "SmartSelector", new RegularClassWriter());
        assertMatchesExpected("expected/generated", "AllTypesSmartSelector");
        assertMatchesExpected("expected/generated", "DepartmentSmartSelector");
        assertMatchesExpected("expected/generated", "MasterSmartSelector");
        assertMatchesExpected("expected/generated", "DetailSmartSelector");
    }

    public void testSaverNaturalKeys() throws Exception {
        generate(naturalKeyTransformers, "freemarker/Saver.ftl", "Saver", new SaverWriter());
        // no Saver for table with composite key
        assertFalse("MasterSaver", new File(packageDir, "MasterSaver.java").exists());
        assertFalse("PlainSaver", new File(packageDir, "PlainSaver.java").exists());
        assertMatchesExpected("expected/natural", "AllTypesSaver");
        assertMatchesExpected("expected/natural", "DepartmentSaver");
        assertMatchesExpected("expected/natural", "DetailSaver");
    }

    public void testSaverGeneratedKeys() throws Exception {
        generate(generatedKeyTransformers, "freemarker/Saver.ftl", "Saver", new SaverWriter());
        // no Saver for table with composite key
        assertFalse("MasterSaver", new File(packageDir, "MasterSaver.java").exists());
        assertFalse("MasterSaver", new File(packageDir, "MasterSaver.java").exists());
        assertMatchesExpected("expected/generated", "AllTypesSaver");
        assertMatchesExpected("expected/generated", "DepartmentSaver");
        assertMatchesExpected("expected/generated", "DetailSaver");
    }

    private final File packageDir = new File("target/test-generation/org/symqle/model");
    private String outputDir = "target/test-generation";

    private void generate(final List<Transformer> transformers, final String templateName, final String suffix, final FreeMarkerClassWriter writer) throws Exception {
        final DataSource dataSource = getDataSource();
        final MetadataReader reader = new MetadataReader();
        reader.setDataSource(dataSource);

        final SchemaSqlModel model = reader.readModel();
        SchemaSqlModel transformed = model;

        for (Transformer transformer : transformers) {
            transformed = transformer.transform(transformed);
        }
        final Map<String, String> packageNames = new HashMap<>();
        packageNames.put("model", "org.symqle.model");
        packageNames.put("dto", "org.symqle.model");
        packageNames.put("dao", "org.symqle.model");
        writer.setTemplateName(templateName);
        writer.setSuffix(suffix);
        writer.setOutputDirectory(outputDir);
        writer.setPackageKey("model");
        writer.writeClasses(transformed, packageNames);
    }

    private void assertMatchesExpected(final String resourceDir, final String fileName) throws Exception {
        final String expected;
        final String actual;
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(resourceDir + "/" + fileName)) {
             expected = IOUtils.toString(resourceAsStream);
        }
        try (InputStream inputStream = new FileInputStream(new File(packageDir, fileName+".java"))) {
            actual = IOUtils.toString(inputStream);
        } catch (FileNotFoundException e) {
            if (!packageDir.exists()) {
                System.err.println(packageDir + "does not exist");
            } else {
                System.err.println(packageDir + ":" +Arrays.asList(packageDir.listFiles()));
            }
            throw e;
        }
        assertEquals(expected, actual);
    }

}
