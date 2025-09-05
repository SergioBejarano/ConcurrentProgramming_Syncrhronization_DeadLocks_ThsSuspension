
## Escuela Colombiana de Ingeniería
### Arquitecturas de Software – ARSW


#### Ejercicio – programación concurrente, condiciones de carrera y sincronización de hilos. EJERCICIO INDIVIDUAL O EN PAREJAS.

---

## Integrantes
- Sergio Andrés Bejarano Rodríguez
- Laura Daniela Rodríguez Sánchez

---

##### Parte I – Antes de terminar la clase.

Control de hilos con wait/notify. Productor/consumidor.

*1. Revise el funcionamiento del programa y ejecútelo. Mientras esto ocurren, ejecute jVisualVM y revise el consumo de CPU del proceso correspondiente. A qué se debe este consumo?, cual es la clase responsable?*


<img width="2879" height="1699" alt="image" src="https://github.com/user-attachments/assets/2a3b1a0e-5974-45b3-b1f7-feffc579cfe0" />

Se ejecutan dos hilos: uno productor, que agrega un elemento a la cola cada segundo, y uno consumidor, que intenta extraer elementos de manera continua dentro de un ciclo infinito. El alto consumo de CPU se debe a que la clase Consumer no implementa ningún mecanismo de espera o sincronización, por lo que consulta constantemente la cola sin dar tiempo a que el productor acumule elementos disponibles.


*2. Haga los ajustes necesarios para que la solución use más eficientemente la CPU, teniendo en cuenta que -por ahora- la producción es lenta y el consumo es rápido. Verifique con JVisualVM que el consumo de CPU se reduzca.*

Se realizaron ajustes a las clases productor y consumidor haciendo uso de los métodos wait() y notifyAll(), lo que resultó en una solución más eficiente:

<img width="2879" height="1694" alt="image" src="https://github.com/user-attachments/assets/1654cf0a-49c3-4da6-9fa7-58eaa961e1b7" />



En el consumidor, se verifica que si hay elementos entonces proceda a tomar el que está en la cabeza de la cola (método `poll()`):

<img width="909" height="386" alt="image" src="https://github.com/user-attachments/assets/b54e4e22-aea5-490a-940c-d2fe46c050c5" />


*3. Haga que ahora el productor produzca muy rápido, y el consumidor consuma lento. Teniendo en cuenta que el productor conoce un límite de Stock (cuantos elementos debería tener, a lo sumo en la cola), haga que dicho límite se respete. Revise el API de la colección usada como cola para ver cómo garantizar que dicho límite no se supere. Verifique que, al poner un límite pequeño para el 'stock', no haya consumo alto de CPU ni errores.*


En el productor, se verifica que no sobrepase el límite:

<img width="1014" height="504" alt="image" src="https://github.com/user-attachments/assets/aaf0c572-27cd-4bc6-ab1c-1540e88f15cd" />

Nota: se deja comentado el tiempo de espera de 1 segundo porque en este punto 3 se pide que el productor produzca más rápido.


En la siguiente imagen se verifica que efectivamente al poner un límite pequeño para el stock, aún así no hay consumo alto de CPU ni errores:

<img width="2879" height="1703" alt="image" src="https://github.com/user-attachments/assets/a662cfbb-d061-436a-8570-106578b5d8c2" />

---

##### Parte II. – Antes de terminar la clase.

Teniendo en cuenta los conceptos vistos de condición de carrera y sincronización, haga una nueva versión -más eficiente- del ejercicio anterior (el buscador de listas negras). En la versión actual, cada hilo se encarga de revisar el host en la totalidad del subconjunto de servidores que le corresponde, de manera que en conjunto se están explorando la totalidad de servidores. Teniendo esto en cuenta, haga que:

- La búsqueda distribuida se detenga (deje de buscar en las listas negras restantes) y retorne la respuesta apenas, en su conjunto, los hilos hayan detectado el número de ocurrencias requerido que determina si un host es confiable o no (_BLACK_LIST_ALARM_COUNT_).
- Lo anterior, garantizando que no se den condiciones de carrera.

En la nueva versión del buscador de listas negras se mejoraron tres partes clave: en HostBlackListSearchThread se añadió la verificación de una condición de parada global (shouldStop) para que cada hilo deje de trabajar apenas se alcance el número de ocurrencias requerido, evitando revisiones innecesarias. En HostBlackListsValidator se reemplazó la lógica de acumulación individual por una coordinación centralizada usando el estado compartido, lo que permite juntar resultados sin condiciones de carrera y detener la búsqueda en conjunto de forma temprana. Finalmente, la nueva clase SharedSearchStatus concentra el conteo global de ocurrencias, la lista de servidores detectados y la bandera de parada, todo sincronizado para garantizar consistencia entre hilos, convirtiéndose en el punto seguro de comunicación entre ellos.

En HostBlackListSearchThread, se agrega condicional preguntando si debería parar o no y si alcanza el límite, acaba la ejecución:

<img width="601" height="197" alt="image" src="https://github.com/user-attachments/assets/390bc111-4980-4bb1-83bc-27282f52ffbe" />

Se actualiza stop en SharedSearchStatus:

<img width="495" height="199" alt="image" src="https://github.com/user-attachments/assets/4d98b6bb-2453-4a59-bf27-769aa8db3ea5" />


---

##### Parte III. – Avance para el martes, antes de clase.

Sincronización y Dead-Locks.

![](http://files.explosm.net/comics/Matt/Bummed-forever.png)

*1. Revise el programa “highlander-simulator”, dispuesto en el paquete edu.eci.arsw.highlandersim. Este es un juego en el que:*
	
* *Se tienen N jugadores inmortales.*
* *Cada jugador conoce a los N-1 jugador restantes.*
* *Cada jugador, permanentemente, ataca a algún otro inmortal. El que primero ataca le resta M puntos de vida a su contrincante, y aumenta en esta misma cantidad sus propios puntos de vida.*
* *El juego podría nunca tener un único ganador. Lo más probable es que al final sólo queden dos, peleando indefinidamente quitando y sumando puntos de vida.*

*2. Revise el código e identifique cómo se implementó la funcionalidad antes indicada. Dada la intención del juego, un invariante debería ser que la sumatoria de los puntos de vida de todos los jugadores siempre sea el mismo(claro está, en un instante de tiempo en el que no esté en proceso una operación de incremento/reducción de tiempo). Para este caso, para N jugadores, cual debería ser este valor?.*

En la clase **ControlFrame**  se tiene un atributo que establece la vida de los jugadores inmortales, esta es:

```java
    private static final int DEFAULT_IMMORTAL_HEALTH = 100;
```

Dado que todos los jugadores inmortales inician con la misma cantidad de vida y 
solo la transfieren de uno a otro, la suma total de los puntos de vida será:

$$ TotalVida = n \times VidaInicial $$


Donde *n* es la cantidad de jugadores y *VidaInicial* es la definida en el atributo *DEFAULT_IMMORTAL_HEALTH*, es decir que para este caso:

$$ TotalVida = n \times 100 $$

*3. Ejecute la aplicación y verifique cómo funcionan las opción ‘pause and check’. ¿Se cumple el invariante?.*

Al ejecutar la aplicación podemos observar que para empezar se crean 3 inmortales. 

<img width="921" height="342" alt="image" src="https://github.com/user-attachments/assets/a937d49e-7255-4710-859d-1f2a330dd4b7" />

Con el botón 'start' iniciamos el juego y con 'pause and check' podemos observar en un instante la vida de cada jugador y su sumatoria.

<img width="921" height="343" alt="image" src="https://github.com/user-attachments/assets/0c0ee3d4-8545-442d-a778-d17c2c0cc6a9" />


<img width="921" height="341" alt="image" src="https://github.com/user-attachments/assets/e905e924-6f44-49b5-883b-a0f4485eef93" />


<img width="921" height="333" alt="image" src="https://github.com/user-attachments/assets/8a426fa3-b804-4537-9b82-8146f24e61e9" />


Podemos observar que la vida total (la cual es la invariante) no se cumple, pues en cada caso se arrojaron distintos valores, unos más alejados de lo esperado (300 en este caso).

*4. Una primera hipótesis para que se presente la condición de carrera para dicha función (pause and check), es que el programa consulta la lista cuyos valores va a imprimir, a la vez que otros hilos modifican sus valores. Para corregir esto, haga lo que sea necesario para que efectivamente, antes de imprimir los resultados actuales, se pausen todos los demás hilos. Adicionalmente, implemente la opción ‘resume’.*

Para implementar que los hilos se pausen se agregó en la clase **Immortal** el atributo **paused**:

```java
    private volatile boolean paused = false;
```

Se agregan los siguientes métodos para pausar y reanudar. Además en el método *run()* se agrega un llamado al método *checkPaused()*:

```java
    public void pause() {
        synchronized (this) {
            paused = true;
        }
    }
    
    public void resumeImmortal() {
        synchronized (this) {
            paused = false;
            notify();
        }
    }
    
    private void checkPaused() {
        synchronized (this) {
            while (paused) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
```
Con esto implementado, se agrega en la clase **ControlFrame** en el ActionListener 
del botón de *Pause And Check* un ciclo donde se ejecuta en cada hilo el método *pause()*
que cambiará el estado de la variable paused y una espera de 50 milisegundos para confirmar que todos los hilos se pausaron 
correctamente. Esto antes de realizar la suma de la vida de cada inmortal.

```java
JButton btnPauseAndCheck = new JButton("Pause and check");
btnPauseAndCheck.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        for (Immortal im : immortals) {
            im.pause();
        }

        try {
            Thread.sleep(50);  
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        int sum = 0;
        for (Immortal im : immortals) {
            sum += im.getHealth();
        }

        statisticsLabel.setText("<html>"+immortals.toString()+"<br>Health sum:"+ sum);
    }
});
```

En el ActionListener del botón de *resume* se hace el mismo ciclo para reanudar los hilos con el método *resumeImmortal()*.

```java
    btnResume.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            for (Immortal im : immortals) {
                im.resumeImmortal();
            }
        }
    });
```

*5. Verifique nuevamente el funcionamiento (haga clic muchas veces en el botón). ¿Se cumple o no el invariante?.*

No, aún no se cumple la invariante pues el valor de la vida total cambia al hacer clic múltiples veces en el botón.
Es posible que dos hilos ataquen a un mismo hilo, generando una condición carrera.

<img width="921" height="338" alt="image" src="https://github.com/user-attachments/assets/36b2db0c-b483-484c-9265-e175c07452c7" />


<img width="921" height="336" alt="image" src="https://github.com/user-attachments/assets/069da2fd-f25d-46f7-8810-a0dc651cd54d" />


<img width="921" height="342" alt="image" src="https://github.com/user-attachments/assets/2aa6ea22-8602-4066-95d3-871b34382c3a" />


*6. Identifique posibles regiones críticas en lo que respecta a la pelea de los inmortales. Implemente una estrategia de bloqueo que evite las condiciones de carrera. Recuerde que si usted requiere usar dos o más ‘locks’ simultáneamente, puede usar bloques sincronizados anidados:*

Se identifica una región crítica en la actualización de puntos de vida de los inmortales durante la ejecución de la pelea,
pues varios hilos pueden estar modificando la vida de un mismo hilo. Es crucial considerar que se puede generar un deadlock
si ambos hilos intentan pelear entre si al mismo tiempo, es decir A pelea con B y B pelea con A.

Así que para implementar una estrategia debemos:

- Sincronizar los accesos a la vida y a los cambios de vida de los inmortales.
- Cuando un inmortal ataca a otro, ambos deben quedar bloqueados en la región crítica, ya que uno quita vida y el otro la gana.
- Para evitar el deadlock, se debe establecer un orden de adquisición de locks.

1. Empezamos implementando un orden de bloqueo usando el nombre d elos inmortales:

```java
    Immortal first, second;
    
    if (this.name.compareTo(i2.name) < 0) {
        first = this;
        second = i2;
    } else {
        first = i2;
        second = this;
    }
```

2. Ahora implementamos bloques sincronizados anidados cuando se cambia el valor de la vida.

```java
    synchronized (first) {
        synchronized (second) {
            if (i2.getHealth() > 0) {
                i2.changeHealth(i2.getHealth() - defaultDamageValue);
                this.health += defaultDamageValue;
                updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
            } else {
                updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
            }
        }
    }
```

3. Por última sincronizamos los métodos que cambian y devuelven la vida de los inmortales.

```java
    public void changeHealth(int v) {
        synchronized (this) {
            health = v;
        }
    }

    public int getHealth() {
        synchronized (this){
            return health;
        }
    }
```

*7. Tras implementar su estrategia, ponga a correr su programa, y ponga atención a si éste se llega a detener. Si es así, use los programas jps y jstack para identificar por qué el programa se detuvo.*

Se pone a correr el programa nuevamente:

<img width="921" height="344" alt="image" src="https://github.com/user-attachments/assets/2afc0264-3353-429a-86f5-5ebdf7e3bbf6" />


<img width="921" height="339" alt="image" src="https://github.com/user-attachments/assets/3b9a1fb2-cca9-4b23-8b09-81c02db074b2" />


<img width="921" height="336" alt="image" src="https://github.com/user-attachments/assets/3ab27165-2ff7-4974-b380-e77c523332b1" />


Se evidencia que ahora la invariante se cumple. El programa no se detuvo y funcionó correctamente con casos de 10 y 50 hilos.

<img width="921" height="342" alt="image" src="https://github.com/user-attachments/assets/f6ddc144-c055-420e-b59b-250ca5da7a7b" />


<img width="921" height="347" alt="image" src="https://github.com/user-attachments/assets/378ab051-6963-48fb-9f98-82f92bd89852" />


*8. Plantee una estrategia para corregir el problema antes identificado (puede revisar de nuevo las páginas 206 y 207 de _Java Concurrency in Practice_).*

El problema no ocurrió ya que se trató el posible deadlock correctamente.

*9. Una vez corregido el problema, rectifique que el programa siga funcionando de manera consistente cuando se ejecutan 100, 1000 o 10000 inmortales. Si en estos casos grandes se empieza a incumplir de nuevo el invariante, debe analizar lo realizado en el paso 4.*

Probamos nuevamente con los casos solicitados, en cada caso se pausa repetidamente para comprobar que la invariante se cumpla:

- 100 inmortales
<img width="921" height="836" alt="image" src="https://github.com/user-attachments/assets/d5c46795-44eb-46ea-ae6f-1247e81d1ca4" />

- 1000 inmortales
<img width="921" height="595" alt="image" src="https://github.com/user-attachments/assets/e21999e8-b603-456c-ba2f-65f97833864e" />

- 10000 inmortales: Por limitaciones del hardware no fue posible ejecutar este caso.


*10. Un elemento molesto para la simulación es que en cierto punto de la misma hay pocos 'inmortales' vivos realizando peleas fallidas con 'inmortales' ya muertos. Es necesario ir suprimiendo los inmortales muertos de la simulación a medida que van muriendo. Para esto:*
	
* *Analizando el esquema de funcionamiento de la simulación, esto podría crear una condición de carrera? Implemente la funcionalidad, ejecute la simulación y observe qué problema se presenta cuando hay muchos 'inmortales' en la misma. Escriba sus conclusiones al respecto en el archivo RESPUESTAS.txt.*

Se podría generar una condición carrera cuando un hilo consulte la lista para elegir un inmortal mientras otro lo elimina de esta.

Se realiza esta implementación inicial:

```java
        synchronized (first) {
            synchronized (second) {
                if (i2.getHealth() > 0) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    this.health += defaultDamageValue;
                    updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
                } else {
                    immortalsPopulation.remove(i2); // Erroneo, pues genera condicion carrera
                    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                }
            }
        }
```

La cual genera casos como este, donde varios hilos escogen un hilo que ya se encuentra "eliminado" de la lista:

<img width="921" height="132" alt="image" src="https://github.com/user-attachments/assets/2640ebf6-9bc7-498b-8588-e34ee9032040" />


* *Corrija el problema anterior __SIN hacer uso de sincronización__, pues volver secuencial el acceso a la lista compartida de inmortales haría extremadamente lenta la simulación.*

Para corregir el problema se realizan las siguientes implementaciones:

- Se cambia la variable que represnta la lista de los inmortales por:
```java
    private static List<Immortal> immortalsPopulation = Collections.synchronizedList(new ArrayList<>());
```

- Método **run()**: Si la vida del inmortal es igual o menor a 0, no ejecuta el código para buscar pelea.

```java
    public void run() {
    while (getHealth() > 0) {

        if (immortalsPopulation.size() <= 1) {
            break;
        }

        int myIndex = immortalsPopulation.indexOf(this);
        if (myIndex == -1) {
            break;
        }

        int nextFighterIndex;
        do {
            nextFighterIndex = r.nextInt(immortalsPopulation.size());
        } while (nextFighterIndex == myIndex || immortalsPopulation.get(nextFighterIndex).getHealth() <= 0);

        Immortal im = immortalsPopulation.get(nextFighterIndex);

        if (im != null) {
            this.fight(im);
        }

        checkPaused();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

Con esto ya podemos comprobar que efectivamente funciona, ya que al morir im0 este deja de aparecer en la 
simulación y su vida aparece como 0.

<img width="921" height="337" alt="image" src="https://github.com/user-attachments/assets/2a6a35ac-1283-4302-981f-9901154c1d44" />

*11. Para finalizar, implemente la opción STOP.*

Se agrega un ActionListener para detener a todos los inmortales y habilitar el botón de start para iniciar un nuevo juego.
```java
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (Immortal im : immortals) {
                    im.stopImmortal();
                }
                immortals.clear();
                btnStart.setEnabled(true);
            }
        });
```
Y en el método *run()*, se cambia la condición del while:
```java
    public void run() {
        while (!stopped) {

            if (immortalsPopulation.size() <= 1) {
                break;
            }

            int myIndex = immortalsPopulation.indexOf(this);
            if (myIndex == -1) {
                break;
            }

            int nextFighterIndex;
            do {
                nextFighterIndex = r.nextInt(immortalsPopulation.size());
            } while (nextFighterIndex == myIndex || immortalsPopulation.get(nextFighterIndex).getHealth() <= 0);

            Immortal im = immortalsPopulation.get(nextFighterIndex);

            if (im != null) {
                this.fight(im);
            }

            checkPaused();

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
```
<!--
### Criterios de evaluación

1. Parte I.
	* Funcional: La simulación de producción/consumidor se ejecuta eficientemente (sin esperas activas).

2. Parte II. (Retomando el laboratorio 1)
	* Se modificó el ejercicio anterior para que los hilos llevaran conjuntamente (compartido) el número de ocurrencias encontradas, y se finalizaran y retornaran el valor en cuanto dicho número de ocurrencias fuera el esperado.
	* Se garantiza que no se den condiciones de carrera modificando el acceso concurrente al valor compartido (número de ocurrencias).


2. Parte III.
	* Diseño:
		- Coordinación de hilos:
			* Para pausar la pelea, se debe lograr que el hilo principal induzca a los otros a que se suspendan a sí mismos. Se debe también tener en cuenta que sólo se debe mostrar la sumatoria de los puntos de vida cuando se asegure que todos los hilos han sido suspendidos.
			* Si para lo anterior se recorre a todo el conjunto de hilos para ver su estado, se evalúa como R, por ser muy ineficiente.
			* Si para lo anterior los hilos manipulan un contador concurrentemente, pero lo hacen sin tener en cuenta que el incremento de un contador no es una operación atómica -es decir, que puede causar una condición de carrera- , se evalúa como R. En este caso se debería sincronizar el acceso, o usar tipos atómicos como AtomicInteger).

		- Consistencia ante la concurrencia
			* Para garantizar la consistencia en la pelea entre dos inmortales, se debe sincronizar el acceso a cualquier otra pelea que involucre a uno, al otro, o a los dos simultáneamente:
			* En los bloques anidados de sincronización requeridos para lo anterior, se debe garantizar que si los mismos locks son usados en dos peleas simultánemante, éstos será usados en el mismo orden para evitar deadlocks.
			* En caso de sincronizar el acceso a la pelea con un LOCK común, se evaluará como M, pues esto hace secuencial todas las peleas.
			* La lista de inmortales debe reducirse en la medida que éstos mueran, pero esta operación debe realizarse SIN sincronización, sino haciendo uso de una colección concurrente (no bloqueante).

	

	* Funcionalidad:
		* Se cumple con el invariante al usar la aplicación con 10, 100 o 1000 hilos.
		* La aplicación puede reanudar y finalizar(stop) su ejecución.
		
		-->

<a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc/4.0/88x31.png" /></a><br />Este contenido hace parte del curso Arquitecturas de Software del programa de Ingeniería de Sistemas de la Escuela Colombiana de Ingeniería, y está licenciado como <a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/">Creative Commons Attribution-NonCommercial 4.0 International License</a>.
