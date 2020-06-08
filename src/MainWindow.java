import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class MainWindow {
	
	private HttpClient con;
	private HttpRequest req;
	private String uriStr;
		
	private Font font;
	private JFrame frame;
	
	private JButton lenBtn;
	private JTextField lenField;
	
	private JTextField indexField;
	private JButton getButton;
	private JButton delButton;
	
	private JButton allButton;
	
	private JTextField xField;
	private JTextField yField;
	private JButton addButton;
	
	private LinkedList<Point> objects;
	
	public MainWindow() throws IOException, InterruptedException, URISyntaxException {
		con = HttpClient.newHttpClient();
		uriStr = "http://localhost:8081/Test/Serv";
		
		objects = new LinkedList<>();
		
		font = new Font("Monospaced", 0, 9);
		
		frame = new JFrame("АВТ-818 Жигулин Чекалова");
		frame.setBounds(400, 20, 640, 480);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		
		Pan p = new Pan(objects);
		p.setLocation(20, 20);
		frame.add(p);
		
		lenBtn = new JButton("Количество объектов");
		lenBtn.setBounds(440, 20, 160, 20);
		lenBtn.setFont(font);
		lenBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					req = HttpRequest.newBuilder()
							.GET()
							.uri(new URI(uriStr+"?command=len")).build();
					HttpResponse<InputStream> resp = con.send(req, HttpResponse.BodyHandlers.ofInputStream());
					DataInputStream dis = new DataInputStream(resp.body());
					lenField.setText(String.valueOf(dis.readInt()));

				} catch (URISyntaxException | IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		frame.add(lenBtn);
		
		lenField = new JTextField();
		lenField.setBounds(440, 40, 160, 20);
		lenField.setEditable(false);
		lenField.setBackground(Color.white);
		lenField.setHorizontalAlignment(JTextField.CENTER);
		frame.add(lenField);
		
		indexField = new JTextField();
		indexField.setBounds(440, 80, 160, 20);
		indexField.setFont(font);
		indexField.setToolTipText("Индекс элемента");
		frame.add(indexField);
		
		getButton = new JButton("Получить объект");
		getButton.setBounds(440, 100, 160, 20);
		getButton.setFont(font);
		getButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String iStr = indexField.getText();
				int i;
				lenBtn.doClick();
				String lenStr = lenField.getText();
				int len;
				try {
					i = Integer.parseInt(iStr);
					len = Integer.parseInt(lenStr);
					if(i < 0 || i >= len)
						throw new NumberFormatException();
				}
				catch(NumberFormatException ex) {
					System.out.println("Неверный индекс");
					return;
				}
				
				try {
					req = HttpRequest.newBuilder()
							.GET()
							.uri(new URI(uriStr+"?command=get&index="+iStr)).build();
					HttpResponse<InputStream> resp = con.send(req, HttpResponse.BodyHandlers.ofInputStream());
					DataInputStream dis = new DataInputStream(resp.body());
					int x;
					int y;
					x = dis.readInt();
					if(x == -1)
						return;
					y = dis.readInt();
					
					while(objects.size() != 0)
						objects.remove();
					
					objects.add(new Point(x, y));
					System.out.println(x + " : " + y);
				} catch (URISyntaxException | IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		frame.add(getButton);
		
		delButton = new JButton("Удалить объект");
		delButton.setBounds(440, 120, 160, 20);
		delButton.setFont(font);
		delButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String iStr = indexField.getText();
				int i;
				lenBtn.doClick();
				String lenStr = lenField.getText();
				int len;
				
				try {
					i = Integer.parseInt(iStr);
					len = Integer.parseInt(lenStr);
					if(i < 0 || i >= len)
						throw new NumberFormatException();
				}
				catch(NumberFormatException ex) {
					System.out.println("Неверный индекс");
					return;
				}
				
				try {
					req = HttpRequest.newBuilder()
							.GET()
							.uri(new URI(uriStr+"?command=del&index="+iStr)).build();
					HttpResponse<InputStream> resp = con.send(req, HttpResponse.BodyHandlers.ofInputStream());
					DataInputStream dis = new DataInputStream(resp.body());
					int res = dis.readInt();
					System.out.println(res);
				} catch (URISyntaxException | IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
				while(objects.size() != 0)
					objects.remove();
				lenBtn.doClick();
			}
		});
		frame.add(delButton);
		
		allButton = new JButton("Получить все объекты");
		allButton.setBounds(440, 160, 160, 20);
		allButton.setFont(font);
		allButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				lenBtn.doClick();
				String lenStr = lenField.getText();
				int len = Integer.parseInt(lenStr);
				
				try {
					req = HttpRequest.newBuilder()
							.GET()
							.uri(new URI(uriStr+"?command=all")).build();
					HttpResponse<InputStream> resp = con.send(req, HttpResponse.BodyHandlers.ofInputStream());
					DataInputStream dis = new DataInputStream(resp.body());
					
					while(objects.size() != 0)
						objects.remove();
					
					for(int i = 0; i < len; i++) {
						int x = dis.readInt();
						int y = dis.readInt();
						objects.add(new Point(x, y));
						System.out.println(x + " : " + y);
					}
				} catch (URISyntaxException | IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		frame.add(allButton);
		
		xField = new JTextField();
		xField.setBounds(440, 200, 70, 20);
		xField.setFont(font);
		xField.setToolTipText("Координата X");
		frame.add(xField);
		
		yField = new JTextField();
		yField.setBounds(530, 200, 70, 20);
		yField.setFont(font);
		yField.setToolTipText("Координата Y");
		frame.add(yField);
		
		addButton = new JButton("Добавить объект");
		addButton.setBounds(440, 220, 160, 20);
		addButton.setFont(font);
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String xStr;
				String yStr;
				int x;
				int y;
				
				try {
					xStr = xField.getText();
					x = Integer.parseInt(xStr);
				
					yStr = yField.getText();
					y = Integer.parseInt(yStr);
				}
				catch (NumberFormatException ex) {
					System.out.println("Неверные координаты");
					return;
				}
				
				try {
					req = HttpRequest.newBuilder()
							.POST(BodyPublishers.ofString(""))
							.uri(URI.create(uriStr+"?command=add&x=" + xStr +"&y=" + yStr))
							.build();
					HttpResponse<InputStream> resp = con.send(req, HttpResponse.BodyHandlers.ofInputStream());
					
					DataInputStream dis = new DataInputStream(resp.body());
					System.out.println(dis.readInt());
					dis.close();
				} catch (IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
				while(objects.size() != 0)
					objects.remove();
				objects.add(new Point(x, y));
				lenBtn.doClick();
			}
		});
		frame.add(addButton);
		
		frame.setVisible(true);
		
		Timer t = new Timer(p);
		t.start();
}
	
	public static void main(String[] args) {
		try {
			new MainWindow();
		} catch (IOException | InterruptedException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
}

class Pan extends JComponent {
	private static final long serialVersionUID = 1L;
	private int w = 400;
	private int h = 400;
	
	private LinkedList<Point> objects;
	
	public Pan(LinkedList<Point> o) {
		setSize(w, h);
		objects = o;
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, w, h);
		
		g.setColor(Color.green);
		for(Point p : objects) {
			g.fillRect(p.x, p.y, 10, 10);
			if(p.x < 400)
				p.x += 5;
			else
				p.x = 0;
		}
	}
}

class Timer extends Thread {
	
	private Pan p;
	
	public Timer(Pan p) {
		this.p = p;
	}
	
	@Override
	public void run() {
		while(!isInterrupted()) {
			p.repaint();
			try {
				sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
