package com.aurea.cleanuptool.statistics.reports

import freemarker.cache.FileTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template

class ReportBuilder {

    private static Configuration configuration
    private static String template

    /**
     * Html report builder
     * @param fileName - file name to produced report
     * @param headers - table headers
     * @param reportTable - report table
     * @param total - total table line
     * @param description - description under table
     */
    static void build(String fileName,
                      String title,
                      List<String> headers,
                      List<StatisticReportLine> reportTable,
                      List<String> total,
                      List<String> description) {

        Map<String, Object> root = new HashMap<>()
        root.put("reportTitle", title)
        root.put("headers", headers)
        root.put("reportTable", reportTable)
        root.put("total", total)
        root.put("description", description)
        Template temp = configuration.getTemplate(template)
        File file = new File(fileName)
        FileOutputStream stream = new FileOutputStream(file)
        Writer out = new OutputStreamWriter(stream)
        temp.process(root, out)
    }

    static void setReportTemplate(String template) {
        configuration = new Configuration(Configuration.VERSION_2_3_23)
        if (template != null && !template.isEmpty()) {
            File file = new File(template)
            configuration.setTemplateLoader(new FileTemplateLoader(new File(file.parent)))
            this.template = file.name
        } else {
            configuration.setClassForTemplateLoading(ReportBuilder.class, "")
            this.template = "tableReport.ftl"
        }
    }
}
