package conecta4;

import java.util.Random;
import java.util.Scanner;

public class Conecta4 {
	/**
	 * Constants
	 */
	static final int FILES = 6;
	static final int COLUMNES = 7;

	/**
	 * Lector del teclat
	 */
	static Scanner lector = new Scanner(System.in);
	/**
	 * Per generar aleatoris
	 */
	static Random aleatori;

	/**
	 * Mètode principal
	 */
	public static void main(String[] args) {

		char[][] taulell = new char[FILES][COLUMNES];
		boolean acabar;
		char torn = 'x', pararTirada = 'n', pararPartida = 'n';
		int columna, maxPartides;
		int comptadorVictoriesX = 0, comptadorVictoriesO = 0, comptadorEmpats = 0;
		int partides;

		aleatori = new Random(System.currentTimeMillis());

		System.out.println("********** JOC CONNETA 4! **********");
		System.out.print("Quantes partides vols jugar?");
		maxPartides = Integer.parseInt(lector.nextLine());

		System.out.print("Vols parar a cada partida (s/n)?");
		pararPartida = lector.nextLine().toLowerCase().charAt(0);
		if (pararPartida != 's')
			pararPartida = 'n';

		System.out.print("Vols parar a cada tirada (s/n)?");
		pararTirada = lector.nextLine().toLowerCase().charAt(0);
		if (pararTirada != 's')
			pararTirada = 'n';
                
                long totaltemps = 0;
                int tirades = 0;

		for (partides = 0; partides < maxPartides; partides++) {
			reiniciaTaulell(taulell);
			System.out.println("COMENÇA LA PARTIDA, PREM ENTER PER TIRAR!");
			pintarTaulell(taulell);
			if (pararPartida == 's') {
				pausa();
			}
			acabar = false;
			while (!acabar) {
				pintarTaulell(taulell);
				if (torn == 'x') {
					long inici = System.currentTimeMillis();
					columna = pensarJugadaX(taulell);
					long fi = System.currentTimeMillis();
					long durada = fi - inici;
                                        totaltemps += durada;
                                        tirades++;
					if (posarPecaAColumna(taulell, 'x', columna)) {
						System.out
								.println("EL JUGADOR CREU 'X' HA TIRAT A LA COLUMNA: ("
										+ (columna + 1)
										+ "). Ha tardat "
										+ durada + " ms");
						torn = 'o';
					}
				} else if (torn == 'o') {
					long inici = System.currentTimeMillis();
					columna = pensarJugadaO(taulell);
					long fi = System.currentTimeMillis();
					long durada = fi - inici;
					if (posarPecaAColumna(taulell, 'o', columna)) {
						System.out
								.println("EL JUGADOR RODONA 'O' HA TIRAT A LA COLUMNA: ("
										+ (columna + 1)
										+ "). Ha tardat "
										+ durada + " ms");
						torn = 'x';
					}
				}
				if (guanyaJugador(taulell, 'x')) {
					pintarTaulell(taulell);
					System.out
							.println("FI DE LA PARTIDA. HA GUANYAT EL JUGADOR X! FELICITATS!");
					comptadorVictoriesX++;
					acabar = true;
					if (pararPartida == 's') {
						pausa();
					}
				} else if (guanyaJugador(taulell, 'o')) {
					pintarTaulell(taulell);
					System.out
							.println("FI DE LA PARTIDA. HA GUANYAT EL JUGADOR O! FELICITATS!");
					comptadorVictoriesO++;
					acabar = true;
					if (pararPartida == 's') {
						pausa();
					}
				} else if (taulellPle(taulell)) {
					comptadorEmpats++;
					acabar = true;
				}
				pintarTaulell(taulell);
				if (pararTirada == 's') {
					pausa();
				}
			}

		}

		System.out.println("PARTIDES JUGADES ---" + partides);
		System.out.println("VICTÒRIES X --------" + comptadorVictoriesX);
		System.out.println("VICTÒRIES O --------" + comptadorVictoriesO);
		System.out.println("EMPATS -------------" + comptadorEmpats);
                System.out.println("MS MITJANA X -----" + totaltemps/tirades);
	}

        //INICI DEL PROGRAMA
        
	/**
	 * Funció que simula el pensament del jugador 'X'
	 * 
	 * @param taulell El taulell del joc
	 * @return la columna on tira la pe�a
	 */
	private static int pensarJugadaX(char[][] taulell) {	
            char jugador = 'x';  // Jugador actual (puedes cambiarlo a 'o' si quieres jugar con 'o')
            char contrari = (jugador == 'x') ? 'o' : 'x';  // Contrario
            
            //Crear array de puntuacions, una per columna
            int[] score = new int[COLUMNES];            
            //Bucle que itera per cada una de les primeres 7 posibilitats
            for (int i = 0; i < COLUMNES; i++) {
                //Crea una copia del taulell on es simulara cada jugada
                char[][] copiaTaulell = copiarTaulell(taulell);
                //Si es posible posar la peca a la columna i, cridar el subprograma minimax
                if (posarPecaAColumna(copiaTaulell, jugador, i)) {
                    score[i] = minimax(copiaTaulell, 0, -99999, 99999, false, jugador, contrari);
                } else {
                    //En cas de que la columna estigui plena, se li assigna una puntuació molt baixa
                    score[i] = -10000;
                }
            }
            
            //Buscar posicio de la puntuacio mes alta i retornar el seu index
            int maxAt = aleatori.nextInt(COLUMNES);
            for (int i = 0; i < score.length; i++) {
                maxAt = score[i] > score[maxAt] ? i : maxAt;
            }            
            return maxAt;
	}
        
        /**
         * Calcula quina es la millor jugada analitzant les 7 seguents jugades que
         * pots fer, de cada d'aquestes les 7 seguents que pot fer el rival, i aixi
         * tantes vegades com profunditat hi hagi.
         * @param taulell Taulell on es simula la partida
         * @param profunditat Cantitat de capes de jugades que es volen calcular
         * @param esMax Per mirar si ha de buscar la puntuacio maxima o minima de la jugada.
         *              La minima sera cuan sigui jugada del oponent, ja que asumirem
         *              pot fer la millor jugada per tant tindrem la pitjor puntuacio.
         *              La maxima sera cuan ens toqui a nosaltres ja que buscarem la nostra
         *              millor jugada.
         * @param jugador Jugador 
         * @param contrari Contrari
         * @return La millor jugada de la columna on hem tirat la fitxa tinguent en compte la profunditat
         *         a la que volem arribar a simular. Es a dir cuants moviments per devant mirarem.
         */
        private static int minimax(char[][] taulell, int profunditat, int alpha, int beta, boolean esMax, char jugador, char contrari) {
                 
            if (guanyaJugador(taulell, jugador)) {
                //En cas de que la jugada fagi guanyar al jugador
                //es retorna 1000, es resta la profunditat perque
                //aixi el cami mes rapid per guanyar tindra mes punts.
                return 100000 - profunditat;
            } else if (guanyaJugador(taulell, contrari)) {
                //En cas de que guanya el contrari, es retorna una
                //puntuacio de -1000, es suma la profunditat perque aixi
                //trii el cami mes llarg per perdre.
                return -1000 + profunditat;
            } else if (taulellPle(taulell) || profunditat >= 5) {
                return 0;
            }
            
            //Comprovar qui esta jugant per asignar correctament el maximitzador o minimitzador
            //Si no es maximitzador es perque s'esta simulant l'oponent
            char fitxa = esMax ? jugador : contrari;
            
            if (esMax) {
                int maxScore = Integer.MIN_VALUE;
                //Bucle que itera per cada una de les posibilitats on es pot tirar
                for (int i = 0; i < COLUMNES; i++) {
                    //Es crea una copia del taulell
                    char[][] copiaTaulell = copiarTaulell(taulell);
                    //Es comprova si es pot posar una fitxa a la columna, i si es pot la coloca.
                    if (posarPecaAColumna(copiaTaulell, fitxa, i)) {
                        //Calcula la puntuacio de la jugada actual, i crida recursivament el subprograma
                        int score = calcularScore(copiaTaulell, i, jugador, contrari);
                        score += minimax(copiaTaulell, profunditat+1, alpha, beta, false, jugador, contrari);
                        //Quedarse-se amb la puntuacio maxima entre la jugada actual i les que ja hem mirat.
                        maxScore = Math.max(maxScore, score);
                        
                        //Implementacio alpha-beta:
                        //Descarta les opcions que son innecesaries
                        //En cas de que la puntuacio actual sigui superior a la puntuacio minima
                        //del node minimitzador, es deixara de buscar en aquesta opcio, 
                        //ja que de totes maneres sera imposible que el trii, perque al passar
                        //per el node minimitzador agafara sempre la puntuacio mes baixa
                        alpha = Math.max(alpha, score);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
                return maxScore;
            } else {
                int minScore = Integer.MAX_VALUE;
                //Bucle que itera per cada una de les posibilitats on es pot tirar
                for (int i = 0; i < COLUMNES; i++) {
                    //Es crea una copia del taulell
                    char[][] copiaTaulell = copiarTaulell(taulell);
                    //Es comprova si es pot posar una fitxa a la columna, i si es pot la coloca.
                    if (posarPecaAColumna(copiaTaulell, fitxa, i)) {
                        //Calcula la puntuacio de la jugada actual, i crida recursivament el subprograma
                        int score = calcularScore(copiaTaulell, i, jugador, contrari);
                        score += minimax(copiaTaulell, profunditat+1, alpha, beta, true, jugador, contrari);
                        //Quedarse-se amb la puntuacio minima entre la jugada actual i les que ja hem mirat.
                        minScore = Math.min(minScore, score);
                        
                        //Implementacio alpha-beta:
                        //Descarta les opcions que son innecesaries
                        //En cas de que la puntuacio actual sigui inferior a la puntuacio maxima
                        //del node maximitzador, es deixara de buscar en aquesta opcio, 
                        //ja que de totes maneres sera imposible que el trii, perque al passar
                        //per el node maximitzador agafara sempre la puntuacio mes alta
                        beta = Math.min(beta, score);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
                return minScore;
            }
        }

        /**
         * Calcula la puntuacio de la jugada basantse amb els seguents criteris:
         * Columna central +3
         * 2 en linea +2 (per posibilitat)
         * 3 en linea +5 (per posibilitat)
         * Guanyar +1000
         * Rival 2 en linea -2 (per posibilitat)
         * Rival 3 en linea -1000
         * @param copiaTaulell Taulell simulat amb la seguent jugada
         * @param columna Columna on s'ha tirat la fitxa
         * @param jugador Jugador que esta jugant
         * @return Puntuacio de la jugada
         */
        private static int calcularScore(char[][] copiaTaulell, int columna, char jugador, char contrari) {
            int score = 0;
            //Comprovar si es columna central
            if (columna == COLUMNES/2){
                score += 3;
            }
                     
            //Comprovar si la jugada es 3 en ralla
            score = score + calcularTresEnRatlla(copiaTaulell,jugador,contrari);
            
            score = score + calcularDosEnRatlla(copiaTaulell,jugador,contrari);
            
            return score;
        }
        
        /**
         * Itera per totes les files de 4 possibles, diagonal, vertical i hexagonalment.
         * Busca cuantes possibilitats de fer 3 en ratla te, es a dir cuantes vegades troba
         * 3 fitxes del jugador i 1 casella buida, i es dona una puntuacio de 5 per cada una.
         * Tambe aprofita per mirar si el contrincat te alguna possibilitat i baixant-nos la puntuacio de la tirada.
         * @param copiaTaulell Taulell on es comprova la jugada
         * @param jugador Jugador
         * @param contrari Contrari
         * @return Puntuacio de la jugada
         */
        private static int calcularTresEnRatlla(char[][] copiaTaulell, char jugador, char contrari) {
            int posibilitats = 0;
            int score = 0;
            //Contar posibilitats horitzontal
            //Bucle que itera per totes les files 
            for (int i = FILES-1; i >= 0; i--) {
                //Bucle perque busqui nomes 4 posibilitats per fila i no es surti dels limits
                for (int j = 0; j < 4; j++) {
                    int contarX = 0, contarO = 0, contarRes = 0;
                    //Bucle que conta dintre de cada posibilitat de 4 en linia cuantes X, O o res hi ha
                    for (int k = 0; k < 4; k++) {
                        switch (copiaTaulell[i][k+j]) {
                            case 'x':
                                contarX++;
                                break;
                            case 'o':
                                contarO++;
                                break;
                            default:
                                contarRes++;
                                break;
                        }
                    }
                    if (jugador == 'x') {
                        //Si troba 3 en ralla suma 1 a posibilitats
                        if (contarX == 3 && contarO == 0) {
                            posibilitats++;
                        }
                        //Si troba 3 en ralla per l'oponent, 
                        if (contarX == 0 && contarO == 3) {
                            score = score - 100;
                        }
                    } else if (jugador == 'o') {
                        //Si troba 3 en ralla suma 1 a posibilitats
                        if (contarO == 3 && contarX == 0) {
                            posibilitats++;
                        }
                        //Si troba 3 en ralla per l'oponent, 
                        if (contarO == 0 && contarX == 3) {
                            score = score - 100;
                        }
                    }                    
                }
            }
            
            //Contar posibilitats de forma vertical
            //Bucle que itera per totes les columnes
            for (int i = 0; i < COLUMNES; i++) {
                //Bucle perque busqui nomes 3 posibilitats per columna i no es surti dels limits
                for (int j = 0; j < 3; j++) {
                    int contarX = 0, contarO = 0, contarRes = 0;
                    //Bucle que conta dintre de cada posibilitat de 4 en linia cuantes X, O o res hi ha
                    for (int k = 0; k < 4; k++) {
                        switch (copiaTaulell[k+j][i]) {
                            case 'x':
                                contarX++;
                                break;
                            case 'o':
                                contarO++;
                                break;
                            default:
                                contarRes++;
                                break;
                        }
                    }
                    if (jugador == 'x') {
                        //Si troba 3 en ralla suma 1 a posibilitats
                        if (contarX == 3 && contarO == 0) {
                            posibilitats++;
                        }
                        //Si troba 3 en ralla per l'oponent, 
                        if (contarX == 0 && contarO == 3) {
                            score = score - 100;
                        }
                    } else if (jugador == 'o') {
                        //Si troba 3 en ralla suma 1 a posibilitats
                        if (contarO == 3 && contarX == 0) {
                            posibilitats++;
                        }
                        //Si troba 3 en ralla per l'oponent, 
                        if (contarO == 0 && contarX == 3) {
                            score = score - 100;
                        }
                    }
                }
            }     
            
            //Contar posibilitats de forma diagonal (cap abaix a la dreta)
            //Bucle que itera entre les 6 files
            for (int i = 0; i < 6; i++) {
                //Bucle que itera entre les 7 columnes
                for (int j = 0; j < 7; j++) {
                    int contarX = 0, contarO = 0, contarRes = 0;
                    //Comprovar si hi ha suficients files cap a baix 
                    //i cap a la dreta per no sortir del array
                    if ( i + 3 < 6 && j + 3 < 7) {
                        //Bucle que conta dintre de cada posibilitat de 4 en linia cuantes X, O o res hi ha
                        for (int k = 0; k < 4; k++) {
                            switch (copiaTaulell[i+k][j+k]) {
                            case 'x':
                                contarX++;
                                break;
                            case 'o':
                                contarO++;
                                break;
                            default:
                                contarRes++;
                                break;
                        }
                        }
                        if (jugador == 'x') {
                            //Si troba 3 en ralla suma 1 a posibilitats
                            if (contarX == 3 && contarO == 0) {
                                posibilitats++;
                            }
                            //Si troba 3 en ralla per l'oponent, 
                            if (contarX == 0 && contarO == 3) {
                                score = score - 100;
                            }
                        } else if (jugador == 'o') {
                            //Si troba 3 en ralla suma 1 a posibilitats
                            if (contarO == 3 && contarX == 0) {
                                posibilitats++;
                            }
                            //Si troba 3 en ralla per l'oponent, 
                            if (contarO == 0 && contarX == 3) {
                                score = score - 100;
                            }
                        }
                    }
                }
            }
            
            //Contar posibilitats de forma diagonal (cap abaix a la esquerra)
            //Bucle que itera entre les 6 files
            for (int i = 0; i < 6; i++) {
                //Bucle que itera entre les 7 columnes
                for (int j = 6; j >= 0; j--) {
                    int contarX = 0, contarO = 0, contarRes = 0;
                    //Comprovar si hi ha suficients files cap a baix 
                    //i cap a la esquerra per no sortir del array
                    if ( i + 3 < 6 && j - 3 >= 0) {
                        //Bucle que conta dintre de cada posibilitat de 4 en linia cuantes X, O o res hi ha
                        for (int k = 0; k < 4; k++) {
                            switch (copiaTaulell[i+k][j-k]) {
                            case 'x':
                                contarX++;
                                break;
                            case 'o':
                                contarO++;
                                break;
                            default:
                                contarRes++;
                                break;
                        }
                        }
                        if (jugador == 'x') {
                            //Si troba 3 en ralla suma 1 a posibilitats
                            if (contarX == 3 && contarO == 0) {
                                posibilitats++;
                            }
                            //Si troba 3 en ralla per l'oponent, 
                            if (contarX == 0 && contarO == 3) {
                                score = score - 100;
                            }
                        } else if (jugador == 'o') {
                            //Si troba 3 en ralla suma 1 a posibilitats
                            if (contarO == 3 && contarX == 0) {
                                posibilitats++;
                            }
                            //Si troba 3 en ralla per l'oponent, 
                            if (contarO == 0 && contarX == 3) {
                                score = score - 100;
                            }
                        }
                    }
                }
            }
            //Dona 5 punts per cada posibilitat de 3 en ralla
            score = score + (posibilitats * 6);
            
            return score;
        }
        
        /**
         * Itera per totes les files de 4 possibles, diagonal, vertical i hexagonalment.
         * Busca cuantes possibilitats de fer 2 en ratla te, es a dir cuantes vegades troba
         * 2 fitxes del jugador i 2 caselles buides, i es dona una puntuacio de 2 per cada una.
         * Tambe aprofita per mirar si el contrincat te alguna possibilitat i baixant-nos la puntuacio de la tirada.
         * @param copiaTaulell Taulell on es comprova la jugada
         * @param jugador Jugador
         * @param contrari Contrari
         * @return Puntuacio de la jugada
         */
        private static int calcularDosEnRatlla(char[][] copiaTaulell, char jugador, char contrari) {
            int posibilitats = 0;
            int score = 0;
            //Contar posibilitats horitzontal
            //Bucle que itera per totes les files 
            for (int i = FILES-1; i >= 0; i--) {
                //Bucle perque busqui nomes 4 posibilitats per fila i no es surti dels limits
                for (int j = 0; j < 4; j++) {
                    int contarX = 0, contarO = 0, contarRes = 0;
                    //Bucle que conta dintre de cada posibilitat de 4 en linia cuantes X, O o res hi ha
                    for (int k = 0; k < 4; k++) {
                        switch (copiaTaulell[i][k+j]) {
                            case 'x':
                                contarX++;
                                break;
                            case 'o':
                                contarO++;
                                break;
                            default:
                                contarRes++;
                                break;
                        }
                    }
                    if (jugador == 'x') {
                        //Si troba 2 en ralla suma 1 a posibilitats
                        if (contarX == 2 && contarO == 0) {
                            posibilitats++;
                        }
                        //Si troba 2 en ralla per l'oponent, 
                        if (contarX == 0 && contarO == 2) {
                            score = score - 2;
                        }
                    } else if (jugador == 'o') {
                        //Si troba 2 en ralla suma 1 a posibilitats
                        if (contarO == 2 && contarX == 0) {
                            posibilitats++;
                        }
                        //Si troba 2 en ralla per l'oponent, 
                        if (contarO == 0 && contarX == 2) {
                            score = score - 2;
                        }
                    }                    
                }
            }
            
            //Contar posibilitats de forma vertical
            //Bucle que itera per totes les columnes
            for (int i = 0; i < COLUMNES; i++) {
                //Bucle perque busqui nomes 3 posibilitats per columna i no es surti dels limits
                for (int j = 0; j < 3; j++) {
                    int contarX = 0, contarO = 0, contarRes = 0;
                    //Bucle que conta dintre de cada posibilitat de 4 en linia cuantes X, O o res hi ha
                    for (int k = 0; k < 4; k++) {
                        switch (copiaTaulell[k+j][i]) {
                            case 'x':
                                contarX++;
                                break;
                            case 'o':
                                contarO++;
                                break;
                            default:
                                contarRes++;
                                break;
                        }
                    }
                    if (jugador == 'x') {
                        //Si troba 2 en ralla suma 1 a posibilitats
                        if (contarX == 2 && contarO == 0) {
                            posibilitats++;
                        }
                        //Si troba 2 en ralla per l'oponent, 
                        if (contarX == 0 && contarO == 2) {
                            score = score - 2;
                        }
                    } else if (jugador == 'o') {
                        //Si troba 2 en ralla suma 1 a posibilitats
                        if (contarO == 2 && contarX == 0) {
                            posibilitats++;
                        }
                        //Si troba 2 en ralla per l'oponent, 
                        if (contarO == 0 && contarX == 2) {
                            score = score - 2;
                        }
                    }
                }
            }     
            
            //Contar posibilitats de forma diagonal (cap abaix a la dreta)
            //Bucle que itera entre les 6 files
            for (int i = 0; i < 6; i++) {
                //Bucle que itera entre les 7 columnes
                for (int j = 0; j < 7; j++) {
                    int contarX = 0, contarO = 0, contarRes = 0;
                    //Comprovar si hi ha suficients files cap a baix 
                    //i cap a la dreta per no sortir del array
                    if ( i + 3 < 6 && j + 3 < 7) {
                        //Bucle que conta dintre de cada posibilitat de 4 en linia cuantes X, O o res hi ha
                        for (int k = 0; k < 4; k++) {
                            switch (copiaTaulell[i+k][j+k]) {
                            case 'x':
                                contarX++;
                                break;
                            case 'o':
                                contarO++;
                                break;
                            default:
                                contarRes++;
                                break;
                        }
                        }
                        if (jugador == 'x') {
                            //Si troba 2 en ralla suma 1 a posibilitats
                            if (contarX == 2 && contarO == 0) {
                                posibilitats++;
                            }
                            //Si troba 2 en ralla per l'oponent, 
                            if (contarX == 0 && contarO == 2) {
                                score = score - 2;
                            }
                        } else if (jugador == 'o') {
                            //Si troba 2 en ralla suma 1 a posibilitats
                            if (contarO == 2 && contarX == 0) {
                                posibilitats++;
                            }
                            //Si troba 2 en ralla per l'oponent, 
                            if (contarO == 0 && contarX == 2) {
                                score = score - 2;
                            }
                        }
                    }
                }
            }
            
            //Contar posibilitats de forma diagonal (cap abaix a la esquerra)
            //Bucle que itera entre les 6 files
            for (int i = 0; i < 6; i++) {
                //Bucle que itera entre les 7 columnes
                for (int j = 6; j >= 0; j--) {
                    int contarX = 0, contarO = 0, contarRes = 0;
                    //Comprovar si hi ha suficients files cap a baix 
                    //i cap a la esquerra per no sortir del array
                    if ( i + 3 < 6 && j - 3 >= 0) {
                        //Bucle que conta dintre de cada posibilitat de 4 en linia cuantes X, O o res hi ha
                        for (int k = 0; k < 4; k++) {
                            switch (copiaTaulell[i+k][j-k]) {
                            case 'x':
                                contarX++;
                                break;
                            case 'o':
                                contarO++;
                                break;
                            default:
                                contarRes++;
                                break;
                        }
                        }
                        if (jugador == 'x') {
                            //Si troba 2 en ralla suma 1 a posibilitats
                            if (contarX == 2 && contarO == 0) {
                                posibilitats++;
                            }
                            //Si troba 2 en ralla per l'oponent, 
                            if (contarX == 0 && contarO == 2) {
                                score = score - 2;
                            }
                        } else if (jugador == 'o') {
                            //Si troba 2 en ralla suma 1 a posibilitats
                            if (contarO == 2 && contarX == 0) {
                                posibilitats++;
                            }
                            //Si troba 2 en ralla per l'oponent, 
                            if (contarO == 0 && contarX == 2) {
                                score = score - 2;
                            }
                        }
                    }
                }
            }
            //Dona 2 punts per cada posibilitat de 2 en ralla
            score = score + (posibilitats * 2);
            
            return score;
        }
        
        /**
         * Crea una copia exacta del taullel
         * @param original Taulell original
         * @return Copia del taulell
         */
        private static char[][] copiarTaulell(char[][] original) {
            char[][] copia = new char[FILES][COLUMNES];
            for (int i = 0; i < FILES; i++) {
                for (int j = 0; j < COLUMNES; j++) {
                    copia[i][j] = original[i][j];
                }
            }
            return copia;
        }
        
        //FI DEL PROGRAMA 
        
        
        /** Aquest codi es la v3 de la IA. Esta aqui per fer proves.
         * 
         * @param copiaTaulell
         * @param columna
         * @param jugador
         * @param contrari
         * @return 
         */
        private static int simularJugada(char[][] copiaTaulell, int columna, char jugador, char contrari) {
            int score = 0;
            int i = FILES - 1;

            // comprovar quina és la primera fila lliure en la columna
            // es comença pel final, ja que és la fila de sota
            while (i>=0 && (copiaTaulell[i][columna] == 'o' || copiaTaulell[i][columna] == 'x')) {
                    i--;
            }
            // si no és una columna vàlida (pq està plena) -> puntuacio mes baixa
            if (i < 0 || i > FILES - 1) {
                    return -1000;
            }
            // Simular el moviment
            if (jugador == 'x') {
                copiaTaulell[i][columna] = jugador;
            } else if (jugador == 'o') {
                copiaTaulell[i][columna] = jugador;
            }
            
            
            score = calcularScore(copiaTaulell, columna, jugador, contrari);
            
            return score;
        }
        
	/**
	 * Funció que simula el pensament del jugador 'O'
	 * 
	 * @param taulell El taulell del joc
	 * @return la columna on tira la pe�a
	 */
	private static int pensarJugadaO(char[][] taulell) {
            //Mode vs huma
            int num;
            System.out.println("Escriu un nombre de 1 a 7: ");
            num = Integer.parseInt(lector.nextLine());
            return num-1;
            
            //Mode vs random
            //return aleatori.nextInt(COLUMNES);
            
            //Mode vs IA stage 3
            /*char jugador = 'o';
            char contrari = 'x';
            int[] score = new int[COLUMNES];
            
            for (int i = 0; i < COLUMNES; i++) {
                char[][] copiaTaulell = copiarTaulell(taulell);
                score[i] = simularJugada(copiaTaulell,i, jugador, contrari);
            }
            
            //Buscar posicio de la puntuacio mes alta
            int maxAt = aleatori.nextInt(COLUMNES);
            for (int i = 0; i < score.length; i++) {
                maxAt = score[i] > score[maxAt] ? i : maxAt;
            }
            return maxAt;*/
	}
        
	/**
	 * Comprova si el taulell és ple
	 * 
	 * @param taulell el taulell del joc
	 * @return true si està ple, false en cas contrari
	 */
	private static boolean taulellPle(char[][] taulell) {
		for (int i = 0; i < FILES; i++) {
			for (int j = 0; j < COLUMNES; j++) {
				if (taulell[i][j] == ' ')
					return false;
			}
		}
		return true;
	}

	/**
	 * Comprova si un jugador guanya
	 * 
	 * @param taulell El taulell del joc
	 * @param jugador El jugador que es vol comprovar
	 * @return true si guanya el jugador, false el cas contrari
	 */
	private static boolean guanyaJugador(char[][] taulell, char jugador) {
		if (guanyaVertical(taulell, jugador)) {
			return true;
		}
		if (guanyaHoritzontal(taulell, jugador)) {
			return true;
		}
		if (guanyaDiagonal(taulell, jugador)) {
			return true;
		}
		return false;
	}

	/**
	 * Comprova si hi ha una diagonal amb 4 en línia
	 * 
	 * @param taulell El taulell del joc
	 * @param jugador El jugador a comprovar
	 * @return true si guanya el jugador, false en cas contrari
	 */
	private static boolean guanyaDiagonal(char[][] taulell, char jugador) {
		int i, j;
		for (i = 0; i < FILES - 3; i++) {
			for (j = 0; j < COLUMNES - 3; j++) {
				if (taulell[i][j] == jugador
						&& taulell[i + 1][j + 1] == jugador
						&& taulell[i + 2][j + 2] == jugador
						&& taulell[i + 3][j + 3] == jugador) {
					return true;
				}
			}
		}

		for (i = 0; i < FILES - 3; i++) {
			for (j = 0; j < COLUMNES - 3; j++) {
				if (taulell[i + 3][j] == jugador
						&& taulell[i + 2][j + 1] == jugador
						&& taulell[i + 1][j + 2] == jugador
						&& taulell[i][j + 3] == jugador) {

					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Comprova si hi ha una horitzontal amb 4 en línia
	 * 
	 * @param taulell El taulell del joc
	 * @param jugador El jugador a comprovar
	 * @return true si guanya el jugador, false en cas contrari
	 */
	private static boolean guanyaHoritzontal(char[][] taulell, char jugador) {
		for (int i = 0; i < FILES; i++) {
			for (int j = 0; j < COLUMNES - 3; j++) {
				if (taulell[i][j] == jugador && taulell[i][j + 1] == jugador
						&& taulell[i][j + 2] == jugador
						&& taulell[i][j + 3] == jugador) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Comprova si hi ha una vertical amb 4 en línia
	 * 
	 * @param taulell El taulell del joc
	 * @param jugador El jugador a comprovar
	 * @return true si guanya el jugador, false en cas contrari
	 */
	private static boolean guanyaVertical(char[][] taulell, char jugador) {
		for (int i = 0; i < FILES - 3; i++) {
			for (int j = 0; j < COLUMNES; j++) {
				if (taulell[i][j] == jugador && taulell[i + 1][j] == jugador
						&& taulell[i + 2][j] == jugador
						&& taulell[i + 3][j] == jugador) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Posa una peça d'un jugador en el taulell
	 * 
	 * @param taulell El taulell del joc
	 * @param jugador El jugador que posa la peça
	 * @param columna La columna on vol posar la peça
	 * @return
	 */
	private static boolean posarPecaAColumna(char[][] taulell, char jugador,
			int columna) {
		int i = FILES - 1;

		// comprovar quina és la primera fila lliure en la columna
		// es comença pel final, ja que és la fila de sota
		while (i>=0 && (taulell[i][columna] == 'o' || taulell[i][columna] == 'x')) {
			i--;
		}
		// si no és una columna vàlida (pq està plena) -> acabar
		if (i < 0 || i > FILES - 1) {
			return false;
		}
		if (jugador == 'o') {
			taulell[i][columna] = 'o';
		}
		if (jugador == 'x') {
			taulell[i][columna] = 'x';
		}
		return true;
	}

	/**
	 * Pinta el taulell en el seu estat actual
	 * 
	 * @param taulell El taulell del joc
	 */
	private static void pintarTaulell(char[][] taulell) {
		int i, j;
                System.out.println();
		System.out.println("- 1 - 2 - 3 - 4 - 5 - 6 - 7 -");
		System.out.println("- - - - - - - - - - - - - - -");
		for (i = 0; i < FILES; i++) {

                    for (j = 0; j < COLUMNES; j++) {
				System.out.print("| ");
				if (taulell[i][j] != 'x' && taulell[i][j] != 'o')
					System.out.print("  ");
				else
					System.out.print(taulell[i][j] + " ");
			}
			System.out.println("|\n- - - - - - - - - - - - - - -");
		}
	}

	/**
	 * Reinicia els valors del taulell
	 * 
	 * @param taulell El taulell del joc
	 */
	private static void reiniciaTaulell(char[][] taulell) {
		for (int i = 0; i < FILES; i++) {
			for (int j = 0; j < COLUMNES; j++) {
				taulell[i][j] = ' ';
			}
		}
	}

	/**
	 * Fa una pausa, esperant que es premi RETORN
	 */
	private static void pausa() {
		lector.nextLine();
	}
}