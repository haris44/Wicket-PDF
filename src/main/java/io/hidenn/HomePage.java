package io.hidenn;

import com.googlecode.wickedcharts.highcharts.options.*;
import com.googlecode.wickedcharts.highcharts.options.color.HexColor;
import com.googlecode.wickedcharts.highcharts.options.series.Series;
import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;
import com.googlecode.wickedcharts.wicket7.highcharts.Chart;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.file.File;

import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableModel;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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

		final String[] columnsData = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov"};
		final HashMap<String, Double[]> dataMap = new HashMap<>();

		dataMap.put("Tokyo", new java.lang.Double[]{7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9});
		dataMap.put("NewYork",  new java.lang.Double[]{0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6});
		dataMap.put("Berlin", new java.lang.Double[]{0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9});



		add(generateChart(columnsData, dataMap));
		add(new Label("version", getApplication().getFrameworkSettings().getVersion()));

		// CREATE JS CALLBACK
		final AbstractDefaultAjaxBehavior click = new AbstractDefaultAjaxBehavior(){
			@Override
			protected void respond(AjaxRequestTarget ajaxRequestTarget) {
				IRequestParameters x = RequestCycle.get().getRequest().getPostParameters();
				String imgBase = x.getParameterValue("base").toString();
				String base64Image = imgBase.split(",")[1];
				byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);

				BufferedImage img = null;
				Image logo = null;
				try {
					img = ImageIO.read(new ByteArrayInputStream(imageBytes));
					logo = ImageIO.read(new File("src/main/webapp/img/aquasys.gif"));
				} catch (IOException e) {
					e.printStackTrace();
				}

				String sourceFileName = "src/main/webapp/templates/rapportTemp.jasper";

				DefaultTableModel tableModel = HomePage.tableModelData();
				JRDataSource dataSource = new JRTableModelDataSource(tableModel);

				Map params = new HashMap();

				params.put("graph", img);
				params.put("logo", logo);
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
					logger.error(e);
				}
			}
		};

		// CREATE LINK
		Link<Void> sub = new BookmarkablePageLink<Void>("sub", HomePage.class) {
			@Override
			protected void onComponentTag(ComponentTag tag) {
				tag.put("onclick", "window.getCharts('"+ click.getCallbackUrl() +"')");
			}
		};


		Link xlsButton = new Link("xls") {
			@Override
			public void onClick() {
				new XlsGenerator("src/main/webapp/output.xls", columnsData, dataMap);
				setResponsePage(new RedirectPage("http://localhost:8080/output.xls"));
			}
		};

		add(sub);
		add(xlsButton);
		add(click);

	}

	private static Chart generateChart(String[] columnsData, HashMap<String, Double[]> dataMap){
		Options options = new Options();
		options.setTitle(new Title("Température"));

		Axis xAxis = new Axis();
		xAxis.setCategories(Arrays.asList(columnsData));
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

		for (Map.Entry<String, Double[]> entry : dataMap.entrySet()) {
			String key = entry.getKey();
			Number[] value = entry.getValue();

			Series<Number> series = new SimpleSeries();
			series.setName(key);
			series.setData(Arrays
					.asList(value));
			options.addSeries(series);
		}

		return new Chart("chart", options);
	}

	private static DefaultTableModel tableModelData() {
		String[] columnNames = {"name", "country"};
		String[][] data = {
				{"G Conger", " Orthopaedic"}, {"klnjhjg", "kjhgjvj"},
		};
		return new DefaultTableModel(data, columnNames);
	}

}
