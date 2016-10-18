package io.hidenn;

import com.googlecode.wickedcharts.highcharts.options.*;
import com.googlecode.wickedcharts.highcharts.options.color.HexColor;
import com.googlecode.wickedcharts.highcharts.options.series.Series;
import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;
import com.googlecode.wickedcharts.wicket7.highcharts.Chart;


import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import org.apache.log4j.Logger;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.string.StringValue;

import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableModel;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class HomePage extends WebPage {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(HomePage.class);

	public HomePage(final PageParameters parameters) {
		super(parameters);
		Options options = new Options();
		options.setTitle(new Title("Température"));

		Axis xAxis = new Axis();
		xAxis.setCategories(Arrays.asList(
				new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}));
		options.setxAxis(xAxis);

		PlotLine plotLines = new PlotLine();
		plotLines.setValue(0f);
		plotLines.setWidth(1);
		plotLines.setColor(new HexColor("#999999"));

		Axis yAxis = new Axis();
		yAxis.setTitle(new Title("Temperature (C)"));
		yAxis.setPlotLines(Collections.singletonList(plotLines));
		options.setyAxis(yAxis);

		Legend legend = new Legend();
		legend.setLayout(LegendLayout.VERTICAL);
		legend.setAlign(HorizontalAlignment.RIGHT);
		legend.setVerticalAlign(VerticalAlignment.TOP);
		legend.setX(-10);
		legend.setY(100);
		legend.setBorderWidth(0);
		options.setLegend(legend);

		Series<Number> series1 = new SimpleSeries();
		series1.setName("Tokyo");
		series1.setData(Arrays
				.asList(new Number[]{7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6}));
		options.addSeries(series1);

		Series<Number> series2 = new SimpleSeries();
		series2.setName("New York");
		series2.setData(Arrays
				.asList(new Number[]{-0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5}));
		options.addSeries(series2);

		Series<Number> series3 = new SimpleSeries();
		series3.setName("Berlin");
		series3.setData(Arrays
				.asList(new Number[]{-0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0}));
		options.addSeries(series3);

		Series<Number> series4 = new SimpleSeries();
		series4.setName("London");
		series4.setData(Arrays
				.asList(new Number[]{3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8}));
		options.addSeries(series4);


		Chart theChart = new Chart("chart", options);

		StringValue temp = RequestCycle.get().getRequest().getQueryParameters().getParameterValue("test");
		logger.info(temp.toString());

		add(theChart);
		add(new Label("version", getApplication().getFrameworkSettings().getVersion()));

		Link pdfButton = new Link("pdf") {
			@Override
			public void onClick() {

				String sourceFileName = "src/main/webapp/templates/template.jasper";

				DefaultTableModel tableModel = HomePage.TableModelData();
				JRDataSource dataSource = new JRTableModelDataSource(tableModel);

				Map params = new HashMap();

				Image img = null;
				try {
					img = ImageIO.read(new File("src/main/webapp/img/aquasys.gif"));
				} catch (IOException e) {
					e.printStackTrace();
				}

				params.put("image", img);
				params.put("ReportTitle", "List of Contacts");
				params.put("author", "Prepared By Alexandre");
				params.put("description", "Ce PDF à été généré grace à Jasper, à partir d'un code Java");

				try {
					System.out.println("Export to PDF ...");
					JasperPrint print = JasperFillManager.fillReport(
							sourceFileName, params, dataSource);
					JasperExportManager.exportReportToPdfFile(print, "src/main/webapp/output.pdf");
					setResponsePage(new RedirectPage("http://localhost:8080/output.pdf"));
				} catch (JRException e) {
					e.printStackTrace();
				}
			}
		};
		add(pdfButton);
	}

	private static DefaultTableModel TableModelData() {
		String[] columnNames = {"name", "country"};
		String[][] data = {
				{"G Conger", " Orthopaedic"}, {"klnjhjg", "kjhgjvj"},
		};
		return new DefaultTableModel(data, columnNames);
	}

}
