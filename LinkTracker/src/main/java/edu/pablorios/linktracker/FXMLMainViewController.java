package edu.pablorios.linktracker;

import edu.pablorios.linktracker.model.WebPage;
import edu.pablorios.linktracker.utils.FileUtils;
import edu.pablorios.linktracker.utils.LinkReader;
import edu.pablorios.linktracker.utils.MessageUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("ALL")
public class FXMLMainViewController {

    @FXML
    private ListView<String> lvListaWebs;
    @FXML
    private ListView<String> lvListaWebsCargadas;
    @FXML
    private Label lbLinksTotal;
    @FXML
    private Label lbPagsTotalCargadas;
    @FXML
    private Label lbPagsProcesadas;

    // Creamos la lista observable de donde recogeremos los datos para mostrarlos en el listView
    // y la de WebPages
    List<WebPage> listaWebs = null;
    ObservableList<String> listaNombresWeb = FXCollections.observableArrayList();
    ObservableList<String> listaObservableWebs;
    // Creamos sheuduled service y executors
    private ScheduledService<Boolean> schedServ;
    private ThreadPoolExecutor executor;

    public void crearExecutor() throws ExecutionException, InterruptedException {
        // Creamos el SheduledService y nuestro contador atomico
        AtomicInteger contadorLinks= new AtomicInteger();
        schedServ = new ScheduledService<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        Platform.runLater(() -> {
                        // Asignamos total de links y pags procesadas a sus label
                        lbPagsProcesadas.setText(String.valueOf((executor.getCompletedTaskCount()-1)));
                        lbLinksTotal.setText(String.valueOf(contadorLinks.get()));
                        });

                        return executor.isTerminated();
                    }
                };
            }
        };
        // Una vez termine ejecutamos nuestro mensaje con el total de webs
        schedServ.setOnSucceeded(e -> {
            if (schedServ.getValue()) {
                MessageUtils.showMessageOK(listaWebs.size());
                schedServ.cancel();
            }
        });

        // Creamos un executor con nuestros futures
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Creamos la lista de futures
        List<Future<List<String>>> future=new ArrayList<>() { };

        // Recorremos nuestra lista de webs y los a??adimos a nuestra lista de futures
        for(int i = 0; i< listaWebs.size(); i++)
        {
            future.add(executor.submit(getCallableWebpages(listaWebs.get(i))));
        }

        executor.execute(()->{
        // Recorremos toda la lista de webs y vamos sumando al contador por cada
        // link procesado y mediante el m??todo setEnlaces asignamos la lista de enlaces
        // recibida de los futures en nuestras webPages de listaWebs
        for(int i = 0; i< listaWebs.size(); i++) {
            List<String> listaRecibida = null;
            try {
                listaRecibida = future.get(i).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            contadorLinks.addAndGet(listaRecibida.size());
            listaWebs.get(i).setEnlaces(listaRecibida);
            }
        });
        executor.shutdown();
        schedServ.restart();
    }


    // M??todo para introducir el fichero
    public void subirFichero() {
        if (listaWebs!=null){
            MessageUtils.showMessageAlreadyLoaded();
            return;
        }
        // Instanciamos filechooser
        FileChooser fc = new FileChooser();

        // Abrimos dialog para subir archivo
        File file = fc.showOpenDialog(null);

        try{
            // Guardamos en nuestras webPages las p??ginas cargadas
            listaWebs = FileUtils.loadPages(Path.of(file.getAbsolutePath()));

            // Recorremos nuestra lista de webSites y las a??adimos a la observable
            for (WebPage listaWeb : listaWebs) {
                listaNombresWeb.add(listaWeb.getWeb());
            }

            // Asignamos al listView finalmente la lista de webs obtenida y su tama??o en el label
            lvListaWebs.setItems(listaNombresWeb);
            lbPagsTotalCargadas.setText(String.valueOf(listaWebs.size()));
        }catch (Exception e){}

    }

    // M??todo para procesar los datos de la carga de links mediante hilos
    public void procesarDatos() throws Exception {

        // Control de errores si ejecutamos procesar sin insertar datos
        if (listaWebs == null) {
            MessageUtils.showError();
        } else
        {
            crearExecutor();
        }
    }

    // M??todo para limpiar la lista
    public void limpiarLista(){
        listaWebs=null;
        listaNombresWeb.clear();
        if (listaObservableWebs!=null)listaObservableWebs.clear();
        lbPagsTotalCargadas.setText("0");
        lbPagsProcesadas.setText("0");
        lbLinksTotal.setText("0");
    }

    // M??todo para crear los hilos (Callable) y obtener las URL de la web enviada por argumentos
    public static Callable<List<String>> getCallableWebpages(WebPage web) {
        return () -> {
            List<String> urlWebs = null;
            try {
                // Obtenemos las url y las guardamos en una lista que devolveremos en un callable
                urlWebs = LinkReader.getLinks(web.getUrl());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return urlWebs;
        };
    }


    // M??todo a ejecutar cada vez que clicamos en el link
    public void handleMouseClick() {
            lvListaWebs.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    try{
                        // Aplicamos el m??todo getEnlaces en el ??ndice seleccionado y lo guardamos en lista
                        List<String> enlaceSeleccionado= listaWebs.get(
                                        lvListaWebs.getSelectionModel().getSelectedIndex())
                                .getEnlaces();

                        // Creamos una lista observable con nuestra lista de enlaces y la mostramos en nuestro listView
                        listaObservableWebs = FXCollections.observableArrayList(enlaceSeleccionado);
                        lvListaWebsCargadas.setItems(listaObservableWebs);
                    }catch(Exception e){}
                }
            });


    }

    // M??todo para salir de la aplicaci??n
    public void salir() {Platform.exit();}
}




