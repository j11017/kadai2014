package com.datareversi.screen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.text.format.DateFormat;

import com.datareversi.Assets;
import com.datareversi.Globals;
import com.datareversi.Theory;
import com.datareversi.framework.Game;
import com.datareversi.framework.Graphics;
import com.datareversi.framework.Input.TouchEvent;
import com.datareversi.framework.Screen;
import com.datareversi.game_screen_process.GameState;
import com.datareversi.game_screen_process.ScreenPosition;

/**
 * @author doi
 * 
 * 必ずModeSelectScreenから呼び出すこと
 *
 */
public class GameScreen extends Screen{
	
	private int boardOnDiscs[][];
	private int reversibleDiscs[][]; // そこに置いたら何個石を裏返せるかを格納しておく
	private int turn;
	private int no_Disc[];
	private int blackNo_Disc[];
	private int whiteNo_Disc[];
	private boolean boardUpDated;
	private boolean gameEnded;
	private boolean screenDrew;
	private boolean endCheck;
	private int evaluations[]; // 評価値
	private boolean settedEvaluation[];
	private int records[][]; // 譜
	private int pass; // 現在のパス連続回数
	private boolean putPosCheck; // 打つ位置を確認するフラグ
	
	
	ScreenPosition screenPosition;

	public GameScreen(Game game) {
		super(game);
		
		screenPosition = new ScreenPosition(Assets.endButtonPixmap.getWidth(),
				Assets.endButtonPixmap.getHeight());
		
		boardOnDiscs = new int[ScreenPosition.getBoardwidth()][ScreenPosition.getBoardheight()];
		reversibleDiscs = new int[ScreenPosition.getBoardwidth()][ScreenPosition.getBoardheight()];
		no_Disc = new int[3];
		blackNo_Disc = new int[2];
		whiteNo_Disc = new int[2];
		evaluations = new int[61];
		settedEvaluation = new boolean[61];
		evaluations[60] = 0;
		settedEvaluation[60] = true;
		for(int i = 0; i < 60; ++i)
			settedEvaluation[i] = false;
		records = new int[60][3];
		gameEnded = false;
		screenDrew = false;
		endCheck = false;
		pass = 0;
		putPosCheck = false;
		
		initBoard(boardOnDiscs, reversibleDiscs, no_Disc);
		turn = GameState.getBlackturn();
		boardUpDated = false;
		
		calcPlacableGrid(boardOnDiscs, reversibleDiscs, turn);
	}

	@Override
	public void update(float deltaTime) {
		if(boardUpDated)
			return;
		
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		int len = touchEvents.size();

		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			
			if (event.type == TouchEvent.TOUCH_UP) {
				
				if(endCheck) {
					if(inBounds(event, ScreenPosition.getEndcheckyesbuttonsquare())) {
						endCheck = false;
						game.setScreen(new TitleScreen(game));
						//game.exit();
						return;
					}
					if(inBounds(event, ScreenPosition.getEndchecknobuttonsquare())) {
						endCheck = false;
						screenDrew = false;
						return;
					}
				} else if(inBounds(event, screenPosition.getEndButtonSquare())) {
					endCheck = true;
					screenDrew = false;
					return;
				} else if(gameEnded && inBounds(event, ScreenPosition.getScreensquare())) {
					postServer();
					game.setScreen(new TitleScreen(game));
					return;
				} else if(Globals.isPlayer[turnToIsPlayer(turn)] && !gameEnded) {
					for(int x = 0; x < ScreenPosition.getBoardwidth(); x++)
						for(int y = 0; y < ScreenPosition.getBoardheight(); y++)
							if(inBounds(event, screenPosition.getGridSquare(x, y))) {
								System.out.println("人が石を打つ");
								System.out.println("X: " + x);
								System.out.println("Y: " + y);
								
								if(reversibleDiscs[x][y] > 0)
									setHumanTheoryEvaluation(boardOnDiscs, no_Disc, x, y);
								boardUpDated = setDisc(x, y, boardOnDiscs, reversibleDiscs, turn,
										boardUpDated, no_Disc, records);
								
								if(boardUpDated)
									turnEndprocess(settedEvaluation, no_Disc, evaluations);
								
								return;
							}
				}
			}
		}
		
		if(!Globals.isPlayer[turnToIsPlayer(turn)] && !gameEnded) {
			ArrayList<Integer> nonPlayerDiscPos = new ArrayList<Integer>();
			nonPlayerDiscPos = getNonPlayerDiscPos(boardOnDiscs, reversibleDiscs, no_Disc, turn,
					evaluations, settedEvaluation);
			if(nonPlayerDiscPos.get(0) != -1)
				boardUpDated = setDisc(nonPlayerDiscPos.get(0), nonPlayerDiscPos.get(1),
						boardOnDiscs, reversibleDiscs, turn, boardUpDated, no_Disc, records);
				
			if(boardUpDated)
				turnEndprocess(settedEvaluation, no_Disc, evaluations);
			
			return;
		}
	}
	
	@Override
	public void present(float deltaTime) {
		if(!screenDrew) {
			Graphics g = game.getGraphics();
			g.drawPixmap(Assets.boardPixmap, 0, 0);
			screenPosition.drawBoard(g, boardOnDiscs);
			if(gameEnded) {
				screenPosition.displayResult(g, no_Disc);
			} else {
				screenPosition.displayTurn(g, turn);
			}
			screenPosition.displayNo_Disc(g, blackNo_Disc, whiteNo_Disc);
			
			g.drawPixmap(Assets.endButtonPixmap, ScreenPosition.getEndbuttonstartx(),
					ScreenPosition.getEndbuttonstarty());
			
			if(endCheck)
				g.drawPixmap(Assets.endCheckDialogPixmap, ScreenPosition.getEndcheckdialogstartx(),
						ScreenPosition.getEndcheckdialogstarty());
			
			screenDrew = true;
		}
		if(boardUpDated) {
			boardUpDated = false;
			
			calcPlacableGrid(boardOnDiscs, reversibleDiscs, turn);
			if(isPlaceable(reversibleDiscs)) {
				pass = 0;
				putPosCheck = true;
			} else {
				pass++;
				screenDrew = false;
				if(pass > 1) {
					
					int finalEvaluation = (no_Disc[GameState.getBlackdisc()]
							== no_Disc[GameState.getWhitedisc()]) ? 0
							: (no_Disc[GameState.getBlackdisc()] > no_Disc[GameState.getWhitedisc()]) 
								? 999
							: -999;
					//setEvaluation(settedEvaluation, no_Disc, evaluations, finalEvaluation);
					for(int i = no_Disc[GameState.getNodisc()]; i >= 0; --i) {
						evaluations[i] = finalEvaluation;
						settedEvaluation[no_Disc[GameState.getNodisc()]] = true;
					}
					
					gameEnd();
					
					screenDrew = false;
					turn = changeTurn(turn);
					
					return;
				}
				turn = changeTurn(turn);
				boardUpDated = true;
			}
		}
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
	
	/**
	 * @param no_Disc
	 * @param boardOnDiscs
	 * @return
	 */
	private int calcEvaluateSquareWeighting(int no_Disc[], int boardOnDiscs[][]) {
		int eval = 0;
		for(int i = 0; i < ScreenPosition.getBoardwidth(); ++i)
			for(int j = 0; j < ScreenPosition.getBoardheight(); ++j)
				if(boardOnDiscs[i][j] == GameState.getBlackdisc())
					eval += GameState.getSquareevaluations()[i][j];
				else if(boardOnDiscs[i][j] == GameState.getWhitedisc())
					eval -= GameState.getSquareevaluations()[i][j];
		
		return eval;
	}
	
	private void setEvaluation(boolean settedEvaluation[], int no_Disc[], int evaluations[], int eval) {
		evaluations[no_Disc[GameState.getNodisc()]] = eval;
		settedEvaluation[no_Disc[GameState.getNodisc()]] = true;
	}
	
	/**
	 * 注意: このメソッドはグローバル変数を用いています。
	 * @param settedEvaluation
	 * @param no_Disc
	 * @param evaluations
	 */
	private void turnEndprocess(boolean settedEvaluation[], int no_Disc[], int evaluations[]) {
		if(!settedEvaluation[no_Disc[GameState.getNodisc()]])
			evaluate(evaluations, no_Disc);
		screenDrew = false;
		turn = changeTurn(turn);
	}
	
	/**
	 * 取得できない場合-1を返す
	 * @param boardOnDiscs
	 * @param reversibleDiscs
	 * @param no_Disc
	 * @param turn
	 * @param evaluations
	 * @param settedEvaluation
	 * @return
	 */
	private ArrayList<Integer> getNonPlayerDiscPos(int boardOnDiscs[][], int reversibleDiscs[][],
			int no_Disc[],int turn, int[] evaluations, boolean[] settedEvaluation) {
		ArrayList<Integer> pos = new ArrayList<Integer>(2);
		pos.add(-1);
		pos.add(-1);
		int posX = 0;
		int posY = 1;
		
		ArrayList< ArrayList<Integer> > tmpPos;
		ArrayList<Integer> tmpPosXY = new ArrayList<Integer>(2);
		tmpPosXY.add(-1);
		tmpPosXY.add(-1);
		int tmpBoardOnDiscs[][] = 
				new int[ScreenPosition.getBoardwidth()][ScreenPosition.getBoardheight()];
		int tmpNo_Disc[] = new int[3];
		int tmpReversibleDiscs[][] = 
				new int[ScreenPosition.getBoardwidth()][ScreenPosition.getBoardheight()];
		int max = -1;
		int tmpTurn = turn;
		boolean tmpSettedEvaluation[] = new boolean[61];
		
		int theoryNum = -1;
		int counter = 0;
		
		switch(no_Disc[GameState.getNodisc()]) {
		case 1: // 最後なら置けるところに置く
			pos = putPuttable(reversibleDiscs);
			reverseDiscs(pos.get(posX), pos.get(posY), tmpBoardOnDiscs, tmpTurn, tmpNo_Disc);
			int evaluation = no_Disc[GameState.getBlackdisc()] == no_Disc[GameState.getWhitedisc()] ? 0
					: no_Disc[GameState.getBlackdisc()] > no_Disc[GameState.getWhitedisc()] ? 999 
					: -999;
			setEvaluation(settedEvaluation, no_Disc, evaluations, evaluation);
			break;
			
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			/* 全探索 
			 * tmpPos[0]とtmpPos[1]が同じバグ
			 * 暫定処理で修正 */
			tmpPos = new ArrayList< ArrayList<Integer> >(); // 置ける場所
			int tmp0X = -1, tmp0Y = -1; // 暫定
			for(int x = 0; x < ScreenPosition.getBoardwidth(); x++) // 置けるところの個数と座標を調べる
				for(int y = 0; y < ScreenPosition.getBoardheight(); y++)
					if(reversibleDiscs[x][y] > 0) {
						tmpPosXY.set(0, x);
						tmpPosXY.set(1, y);
						if(counter  == 0) { // 暫定
							tmp0X = x;
							tmp0Y = y;
						}
						tmpPos.add(new ArrayList<Integer>());
						tmpPos.set(counter, tmpPosXY);
						counter++;
					}
			counter = 0;
			if(tmpPos.size() == 0) { // 置ける場所が無い
				ArrayList<Integer> res = new ArrayList<Integer>(2);
				res.add(-1);
				res.add(-1);
				return res;
			} else { // 置ける場所がある
				tmpPosXY.set(0, tmp0X); // 暫定
				tmpPosXY.set(1, tmp0Y); // 暫定
				tmpPos.set(0, tmpPosXY); // 暫定
				for(ArrayList<Integer> i : tmpPos) {
					
					for(int x = 0; x < ScreenPosition.getBoardwidth(); x++) // 現在の盤面を複製する
						for(int y = 0; y < ScreenPosition.getBoardheight(); y++) {
							tmpBoardOnDiscs[x][y] = boardOnDiscs[x][y];
							tmpReversibleDiscs[x][y] = reversibleDiscs[x][y];
						}
					for(int j = 0; j < tmpNo_Disc.length; j++) // 石と空きマスの数を複製する
						tmpNo_Disc[j] = no_Disc[j];
					
					reverseDiscs(i.get(posX), i.get(posY),
							tmpBoardOnDiscs, tmpTurn, tmpNo_Disc); // 仮に置いてみる(深さ優先探索)
					tmpTurn = getAnotherDisc(tmpTurn); // 次の手番を考える
					calcPlacableGrid(tmpBoardOnDiscs, tmpReversibleDiscs, tmpTurn);
					if(!isPlaceable(tmpReversibleDiscs)) { // 石を置けない
						tmpTurn = changeTurn(tmpTurn);
						calcPlacableGrid(tmpBoardOnDiscs, tmpReversibleDiscs, tmpTurn);
						if(!isPlaceable(tmpReversibleDiscs)) { // 石を置けない
							if(tmpNo_Disc[tmpTurn] > tmpNo_Disc[getAnotherDisc(tmpTurn)]) {
								return i;
							} else if(max == -1) {
								max = 0;
								pos.set(posX, i.get(posX));
								pos.set(posY, i.get(posY));
							}
						} else {
							getNonPlayerDiscPos(tmpBoardOnDiscs, tmpReversibleDiscs,
								tmpNo_Disc, tmpTurn, evaluations, tmpSettedEvaluation); // 再帰的に計算する
						}
					} else {
						getNonPlayerDiscPos(tmpBoardOnDiscs, tmpReversibleDiscs,
							tmpNo_Disc, tmpTurn, evaluations, tmpSettedEvaluation); // 再帰的に計算する
					}
					
					
					if(i.get(posX) != -1) {
						reverseDiscs(i.get(posX), i.get(posY),
								tmpBoardOnDiscs, tmpTurn, tmpNo_Disc);
					
						if(tmpNo_Disc[tmpTurn] > max) { // 最終的に一番取れる方に置く
							pos.set(posX, i.get(posX));
							pos.set(posY, i.get(posY));
							max = tmpNo_Disc[tmpTurn];
							
							int tmpEvaluation = (max == 32) ? 0
									: (max > 32)  ? 999
									: -999;
							setEvaluation(settedEvaluation, no_Disc, evaluations, tmpEvaluation);
						}
					}
					
				}
			}
			break;
			
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
		case 20:
		case 21:
		case 22:
		case 23:
		case 24:
		case 25:
		case 26:
		case 27:
		case 28:
		case 29:
		case 30:
		case 31:
		case 32:
		case 33:
		case 34:
		case 35:
		case 36:
			tmpPos = new ArrayList< ArrayList<Integer> >(); // 置ける場所
			for(int x = 0; x < ScreenPosition.getBoardwidth(); x++) // 置けるところの個数と座標を調べる
				for(int y = 0; y < ScreenPosition.getBoardheight(); y++)
					if(reversibleDiscs[x][y] > 0) {
						tmpPosXY.set(0, x);
						tmpPosXY.set(1, y);
						tmpPos.add(new ArrayList<Integer>());
						tmpPos.set(counter, tmpPosXY);
						counter++;
					}
			counter = 0;
			if(tmpPos.size() == 0) { // 置ける場所が無い
				return (ArrayList<Integer>) Arrays.asList(-1, -1);
			} else { // 置ける場所がある
				int maxEvaluation = -10000;
				for(ArrayList<Integer> i : tmpPos) {
				
					int tmpEvaluation = -10000;
					
					for(int x = 0; x < ScreenPosition.getBoardwidth(); x++) // 現在の盤面を複製する
						for(int y = 0; y < ScreenPosition.getBoardheight(); y++) {
							tmpBoardOnDiscs[x][y] = boardOnDiscs[x][y];
							tmpReversibleDiscs[x][y] = reversibleDiscs[x][y];
						}
					for(int j = 0; j < tmpNo_Disc.length; j++) // 石と空きマスの数を複製する
						tmpNo_Disc[j] = no_Disc[j];
					
					reverseDiscs(i.get(posX), i.get(posY),
							tmpBoardOnDiscs, tmpTurn, tmpNo_Disc); // 仮に置いてみる
					tmpTurn = getAnotherDisc(tmpTurn); // 次の手番を考える
					calcPlacableGrid(tmpBoardOnDiscs, tmpReversibleDiscs, tmpTurn);
					if(!isPlaceable(tmpReversibleDiscs)) { // 石を置けない
						tmpTurn = changeTurn(tmpTurn);
						calcPlacableGrid(tmpBoardOnDiscs, tmpReversibleDiscs, tmpTurn);
						if(!isPlaceable(tmpReversibleDiscs)) { // 石を置けない
							if(tmpNo_Disc[tmpTurn] > tmpNo_Disc[getAnotherDisc(tmpTurn)]) {
								return i;
							} else if(max == -1) {
								max = 0;
								pos.set(posX, i.get(posX));
								pos.set(posY, i.get(posY));
							}
						} else {
							tmpEvaluation = calcEvaluateSquareWeighting(tmpNo_Disc, tmpBoardOnDiscs);
						}
					} else {
						tmpEvaluation = calcEvaluateSquareWeighting(tmpNo_Disc, tmpBoardOnDiscs);
					}
					
					
					if(i.get(posX) != -1) {
						reverseDiscs(i.get(posX), i.get(posY),
								tmpBoardOnDiscs, tmpTurn, tmpNo_Disc);
					
						if(tmpEvaluation > maxEvaluation) { // 最終的に一番取れる方に置く
							pos.set(posX, i.get(posX));
							pos.set(posY, i.get(posY));
							maxEvaluation = tmpEvaluation;
						}
					}
					
				}
				setEvaluation(settedEvaluation, no_Disc, evaluations, maxEvaluation);
			}
			break;
			/*pos = putMinReverse(reversibleDiscs);
			break;*/
		
		case 37:
		case 38:
		case 39:
		case 40:
		case 41:
		case 42:
		case 43:
		case 44:
		case 45:
		case 46:
		case 47:
		case 48:
		case 49:
		case 50:
		case 51:
		case 52:
		case 53:
		case 54:
		case 55:
		case 56:
		case 57:
		case 58:
		case 59:
		case 60:
			pos = getTheoryPos(boardOnDiscs, no_Disc, evaluations, settedEvaluation);
			if(pos.get(posX) == -1)
				pos = putMinReverse(reversibleDiscs);
			break;
			
		default:
			pos = putPuttable(reversibleDiscs);
			break;
		}
		
		return pos;
	}
	
	
	/**
	 * boardOnDiscsの一行をTheory形式に変換する
	 * @param line
	 * @return
	 */
	private int convertLineToBit(int[] line) {
		int ret = 0;
		for(final int i : line) {
			ret <<= 2;
			ret += i;
		}
		return ret;
	}
	
	private void setHumanTheoryEvaluation(int[][] boardOnDiscs, int[] no_Disc, int xPos, int yPos) {
		int theoryNum = getHumanTheoryNum(boardOnDiscs,
				Theory.theory[no_Disc[GameState.getNodisc()] - 1], xPos, yPos);
		if(theoryNum != -1) {
			evaluations[no_Disc[GameState.getNodisc()] - 1] 
					= Theory.theory[no_Disc[GameState.getNodisc()] - 1][theoryNum][Theory.evaluation];
			settedEvaluation[no_Disc[GameState.getNodisc()] - 1] = true;
		} else {
			theoryNum = getHumanReverseTheoryNum(boardOnDiscs,
					Theory.theory[no_Disc[GameState.getNodisc()] - 1], xPos, yPos);
			if(theoryNum != -1) {
				evaluations[no_Disc[GameState.getNodisc()] - 1] 
						= Theory.theory[no_Disc[GameState.getNodisc()] - 1][theoryNum]
								[Theory.evaluation];
				settedEvaluation[no_Disc[GameState.getNodisc()] - 1] = true;
			}
		}
	}
	
	private ArrayList<Integer> getTheoryPos(int[][] boardOnDiscs, int[] no_Disc, int[] evaluations,
			boolean[] settedEvaluation) {
		ArrayList<Integer> pos = new ArrayList<Integer>(2);
		pos.add(-1);
		pos.add(-1);
		int posX = 0;
		int posY = 1;
		
		int theoryNum = getApplicableTheoryNum(boardOnDiscs, 
				Theory.theory[no_Disc[GameState.getNodisc()] - 1]);
		
		if(theoryNum != -1) {
			pos.set(posX, Theory.theory[no_Disc[GameState.getNodisc()] - 1][theoryNum][Theory.xPos]);
			pos.set(posY, Theory.theory[no_Disc[GameState.getNodisc()] - 1][theoryNum][Theory.yPos]);
			evaluations[no_Disc[GameState.getNodisc()] - 1] 
					= Theory.theory[no_Disc[GameState.getNodisc()] - 1][theoryNum][Theory.evaluation];
			settedEvaluation[no_Disc[GameState.getNodisc()] - 1] = true;
		} else {
			theoryNum = getReverseApplicableTheoryNum(boardOnDiscs, 
					Theory.theory[no_Disc[GameState.getNodisc()] - 1]);
			if(theoryNum != -1) {
				pos.set(posX, 
						Theory.theory[no_Disc[GameState.getNodisc()] - 1][theoryNum][Theory.xPos] ^ 7);
				pos.set(posY, 
						Theory.theory[no_Disc[GameState.getNodisc()] - 1][theoryNum][Theory.yPos] ^ 7);
				evaluations[no_Disc[GameState.getNodisc()] - 1] 
						= Theory.theory[no_Disc[GameState.getNodisc()] - 1][theoryNum]
								[Theory.evaluation];
				settedEvaluation[no_Disc[GameState.getNodisc()] - 1] = true;
			}
		}
		
		return pos;
	}
	
	private int getHumanTheoryNum(int[][] boardOnDiscs, int[][] theory, int xPos, int yPos) {
		int theoryNum = -1;
		
		int[][] yxBoardOnDiscs =
				new int[ScreenPosition.getBoardheight()][ScreenPosition.getBoardwidth()];
		for(int x = 0; x < boardOnDiscs.length; ++x) {
			for(int y = 0; y < boardOnDiscs[x].length; ++y) {
				yxBoardOnDiscs[x][y] = boardOnDiscs[y][x];
			}
		}
		
		for(int i = 0; i < theory.length && theoryNum == -1; ++i) {
			for(int line = 0; line < boardOnDiscs.length; ++line)
				if (theory[i][line] == convertLineToBit(yxBoardOnDiscs[line])) {
					theoryNum = i;
				} else {
					theoryNum = -1;
					break;
				}
			if(theoryNum != -1) {
				if(theory[i][Theory.xPos] != xPos || theory[i][Theory.yPos] != yPos) {
					theoryNum = -1;
				}
			}
		}
		return theoryNum;
	}
	
	private int getHumanReverseTheoryNum(int[][] boardOnDiscs, int[][] theory, int xPos, int yPos) {
		int theoryNum = -1;
		
		int[][] yxBoardOnDiscs = 
				new int[ScreenPosition.getBoardheight()][ScreenPosition.getBoardwidth()];
		for(int x = 0; x < boardOnDiscs.length; ++x) {
			for(int y = 0; y < boardOnDiscs[x].length; ++y) {
				yxBoardOnDiscs[x ^ 7][y ^ 7] = boardOnDiscs[y][x];
			}
		}
		
		for(int i = 0; i < theory.length && theoryNum == -1; ++i) {
			for(int line = 0; line < boardOnDiscs.length; ++line)
				if (theory[i][line] == convertLineToBit(yxBoardOnDiscs[line])) {
					theoryNum = i;
				} else {
					theoryNum = -1;
					break;
				}
			if(theoryNum != -1) {
				if(theory[i][Theory.xPos] != xPos || theory[i][Theory.yPos] != yPos) {
					theoryNum = -1;
				}
			}
		}
		return theoryNum;
	}
	
	/**
	 * 適用可能な定石番号を得る
	 * @param boardOnDiscs
	 * @param theory
	 * @return
	 */
	private int getApplicableTheoryNum(int[][] boardOnDiscs, int[][] theory) {
		int theoryNum = -1;
		
		int[][] yxBoardOnDiscs =
				new int[ScreenPosition.getBoardheight()][ScreenPosition.getBoardwidth()];
		for(int x = 0; x < boardOnDiscs.length; ++x) {
			for(int y = 0; y < boardOnDiscs[x].length; ++y) {
				yxBoardOnDiscs[x][y] = boardOnDiscs[y][x];
			}
		}
		
		for(int i = 0; i < theory.length && theoryNum == -1; ++i) {
			for(int line = 0; line < boardOnDiscs.length; ++line)
				if (theory[i][line] == convertLineToBit(yxBoardOnDiscs[line])) {
					theoryNum = i;
				} else {
					theoryNum = -1;
					break;
				}
		}
		return theoryNum;
	}
	
	/**
	 * 盤面反転状態で適用可能な定石番号を得る
	 * @param boardOnDiscs
	 * @param theory
	 * @return
	 */
	private int getReverseApplicableTheoryNum(int[][] boardOnDiscs, int[][] theory) {
		int theoryNum = -1;
		
		int[][] yxBoardOnDiscs = 
				new int[ScreenPosition.getBoardheight()][ScreenPosition.getBoardwidth()];
		for(int x = 0; x < boardOnDiscs.length; ++x) {
			for(int y = 0; y < boardOnDiscs[x].length; ++y) {
				yxBoardOnDiscs[x ^ 7][y ^ 7] = boardOnDiscs[y][x];
			}
		}
		
		for(int i = 0; i < theory.length && theoryNum == -1; ++i) {
			for(int line = 0; line < boardOnDiscs.length; ++line)
				if (theory[i][line] == convertLineToBit(yxBoardOnDiscs[line])) {
					theoryNum = i;
				} else {
					theoryNum = -1;
					break;
				}
		}
		return theoryNum;
	}
	
	/**
	 * 置けるところに置く
	 * @param reversibleDiscs
	 * @return
	 */
	private ArrayList<Integer> putPuttable(int[][] reversibleDiscs) {
		ArrayList<Integer> pos = new ArrayList<Integer>(2);
		pos.add(-1);
		pos.add(-1);
		
		for(int x = 0; x < ScreenPosition.getBoardwidth(); x++)
			for(int y = 0; y < ScreenPosition.getBoardheight(); y++)
				if(reversibleDiscs[x][y] > 0) {
					pos.set(0, x);
					pos.set(1, y);
					break;
				}
		return pos;
	}
	
	/**
	 * 一番裏返せるところに置く
	 * @param reversibleDiscs
	 * @return
	 */
	private ArrayList<Integer> putMaxReverse(int[][] reversibleDiscs) {
		ArrayList<Integer> pos = new ArrayList<Integer>(2);
		pos.add(-1);
		pos.add(-1);
		int max = 0;
		
		for(int x = 0; x < ScreenPosition.getBoardwidth(); x++)
			for(int y = 0; y < ScreenPosition.getBoardheight(); y++)
				if(reversibleDiscs[x][y] > max) {
					pos.set(0, x);
					pos.set(1, y);
					max = reversibleDiscs[x][y];
				}
		return pos;
	}
	
	/**
	 * 一番裏返せないところに置く
	 * @param reversibleDiscs
	 * @return
	 */
	private ArrayList<Integer> putMinReverse(int[][] reversibleDiscs) {
		ArrayList<Integer> pos = new ArrayList<Integer>(2);
		pos.add(-1);
		pos.add(-1);
		int min = 99;
		
		for(int x = 0; x < ScreenPosition.getBoardwidth(); x++)
			for(int y = 0; y < ScreenPosition.getBoardheight(); y++)
				if(reversibleDiscs[x][y] > 0 && reversibleDiscs[x][y] < min) {
					pos.set(0, x);
					pos.set(1, y);
					min = reversibleDiscs[x][y];
				}
		return pos;
	}
	
	private boolean isPlaceable(int reversibleDiscs[][]) {
		for(final int[] i : reversibleDiscs)
			for(final int j : i)
				if(j != 0)
					return true;
		return false;
	}
	
	private void initReversibleDiscs(int reversibleDiscs[][]) {
		for(int x = 0; x < ScreenPosition.getBoardwidth(); x++) {
			for(int y = 0; y < ScreenPosition.getBoardheight(); y++) {
				reversibleDiscs[x][y] = 0;
			}
		}
	}
	
	private void splitNo_Disc(int no_Disc[], int blackNo_Disc[], int whiteNo_Disc[]) {
		blackNo_Disc[0] = no_Disc[GameState.getBlackdisc()] / 10;
		blackNo_Disc[1] = no_Disc[GameState.getBlackdisc()] % 10;
		
		whiteNo_Disc[0] = no_Disc[GameState.getWhitedisc()] / 10;
		whiteNo_Disc[1] = no_Disc[GameState.getWhitedisc()] % 10;
	}
	
	private void initBoard(int boardOnDiscs[][], int reversibleDiscs[][], int no_Disc[]) {
		no_Disc[GameState.getNodisc()] = 60;
		no_Disc[GameState.getBlackdisc()] = 2;
		no_Disc[GameState.getWhitedisc()] = 2;
		splitNo_Disc(no_Disc, blackNo_Disc, whiteNo_Disc);
		
		for(int x = 0; x < ScreenPosition.getBoardwidth(); x++) {
			for(int y = 0; y < ScreenPosition.getBoardheight(); y++) {
				boardOnDiscs[x][y] = GameState.getNodisc();
				reversibleDiscs[x][y] = 0;
			}
		}
		boardOnDiscs[3][3] = GameState.getWhitedisc();
		boardOnDiscs[3][4] = GameState.getBlackdisc();
		boardOnDiscs[4][3] = GameState.getBlackdisc();
		boardOnDiscs[4][4] = GameState.getWhitedisc();
	}
	
	private int changeTurn(int turn) {
		return GameState.getBlackturn() + GameState.getWhiteturn() - turn;
	}
	
	private void evaluate(int evaluation[], int no_Disc[]) { // 仮の形勢判断
		evaluation[no_Disc[GameState.getNodisc()]] 
				= no_Disc[GameState.getBlackdisc()] - no_Disc[GameState.getWhitedisc()];
		settedEvaluation[no_Disc[GameState.getNodisc()]] = true;
	}
	
	/**
	 * そこに石を打ったら裏返せる枚数をreversibleDiscsに格納する。
	 * @param boardOnDiscs
	 * @param reversibleDiscs
	 * @param turn
	 */
	private void calcPlacableGrid(int boardOnDiscs[][], int reversibleDiscs[][], int turn) {
		initReversibleDiscs(reversibleDiscs);
		int playerDisc = getPlayerDisc(turn);
		
		for(int x = 0; x < ScreenPosition.getBoardwidth(); x++)
			for(int y = 0; y < ScreenPosition.getBoardheight(); y++)
				if(boardOnDiscs[x][y] == playerDisc)
					for(int vX = - 1; vX <= 1; vX++) // 八方向で行う
						for(int vY = -1; vY <= 1; vY++)
							if(vX != 0 || vY != 0) // 向きが0の場合は計算しない
								calcNo_ReversibleDiscs(x, y, boardOnDiscs, reversibleDiscs, turn,
										vX, vY, playerDisc);
	}
	
	private void calcNo_ReversibleDiscs(int x, int y, int boardOnDiscs[][], int reversibleDiscs[][],
			int turn, int vectorX, int vectorY, int playerDisc) {
		int nonPlayerDisc = getAnotherDisc(playerDisc);
		
		int tX = x + vectorX;
		int tY = y + vectorY;
		boolean isNonPlayerDisc = false;
		while(isBoardRange(tX, tY) && boardOnDiscs[tX][tY] == nonPlayerDisc) {
			tX += vectorX;
			tY += vectorY;
			isNonPlayerDisc = true;
		}
		if(isBoardRange(tX, tY) && boardOnDiscs[tX][tY] != playerDisc && isNonPlayerDisc)
			reversibleDiscs[tX][tY] += (vectorX != 0)
						? (tX - x) * vectorX - 1
						: (tY - y) * vectorY - 1;
	}
	
	private boolean isBoardRange(int x, int y) {
		return x >= 0 && y >= 0 && x < ScreenPosition.getBoardwidth() 
				&& y < ScreenPosition.getBoardheight();
	}
	
	private boolean setDisc(int x, int y, int boardOnDiscs[][], int reversibleDiscs[][], int turn,
			boolean boardUpDated, int no_Disc[], int records[][]) {
		
		if(reversibleDiscs[x][y] > 0) {
			int move = 60 - no_Disc[GameState.getNodisc()];
			records[move][0] = x;
			records[move][1] = y;
			records[move][2] = turn;
			Globals.position[move] = String.valueOf(y * 8 + x);
			Globals.turn[move] = String.valueOf(turn);
			Date date = new Date();
			Globals.moveTime[move] = String.valueOf(DateFormat.format(Globals.datePattern, date));
			reverseDiscs(x, y, boardOnDiscs, turn, no_Disc);
			boardUpDated = true;
		}
		
		return boardUpDated;
	}
	
	private void reverseDiscs(int x, int y, int boardOnDiscs[][], int turn, int no_Disc[]) {
		no_Disc[GameState.getNodisc()]--;
		no_Disc[turn]++;
		for(int vX = - 1; vX <= 1; vX++) // 八方向で行う
			for(int vY = -1; vY <= 1; vY++)
				if(vX != 0 || vY != 0) { // 向きが0の場合は計算しない
					reverseDirectionDiscs(x, y, boardOnDiscs, vX, vY, turn, no_Disc);
				}
		
		splitNo_Disc(no_Disc, blackNo_Disc, whiteNo_Disc);
	}
	
	private void reverseDirectionDiscs(int x, int y, int boardOnDiscs[][], int vectorX,
			int vectorY, int turn, int no_Disc[]) {
		int playerDisc = getPlayerDisc(turn);
		int nonPlayerDisc = getAnotherDisc(playerDisc);
		
		int tX = x + vectorX;
		int tY = y + vectorY;
		boolean isNonPlayerDisc = false;
		while(isBoardRange(tX, tY) && boardOnDiscs[tX][tY] == nonPlayerDisc) {
			tX += vectorX;
			tY += vectorY;
			isNonPlayerDisc = true;
		}
		if(isBoardRange(tX, tY) && boardOnDiscs[tX][tY] == playerDisc && isNonPlayerDisc) {
			boardOnDiscs[x][y] = playerDisc;
			int loopCounter = (vectorX != 0)
					? (tX - x) * vectorX
					: (tY - y) * vectorY;
			loopCounter--;
			
			while(loopCounter > 0) {
				no_Disc[nonPlayerDisc]--;
				no_Disc[playerDisc]++;
				boardOnDiscs[x + vectorX * loopCounter][y + vectorY * loopCounter] = playerDisc;
				loopCounter--;
			}
		}
	}
	
	private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
		return event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1;
	}
	
	private boolean inBounds(TouchEvent event, int square[]) {
		return inBounds(event, square[0], square[1], square[2], square[3]);
	}
	
	private int getPlayerDisc(int turn) {
		return (turn == GameState.getBlackturn()) ? GameState.getBlackdisc() : GameState.getWhitedisc();
	}
	
	private int getAnotherDisc(int playerDisc) {
		return GameState.getBlackdisc() + GameState.getWhitedisc() - playerDisc;
	}
	
	private int turnToIsPlayer(int turn) {
		return turn - GameState.getBlackturn() + GameState.getFirstmove();
	}
	
	private void postServer() {
		//-----[クライアント設定]
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(GameState.getUrl());
		
		//-----[POST送信するデータを格納]
		ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("mode", Globals.mode));
		nameValuePair.add(new BasicNameValuePair("start_time", Globals.startTime));
		nameValuePair.add(new BasicNameValuePair("end_time", Globals.endTime));
		for(int i = 0; i < 60; ++i) {
			nameValuePair.add( new BasicNameValuePair("turn" + i, Globals.turn[i]));
			nameValuePair.add( new BasicNameValuePair("position" + i, Globals.position[i]));
			nameValuePair.add( new BasicNameValuePair("evaluation_value" + i,
					Globals.evaluationValue[i]));
			nameValuePair.add( new BasicNameValuePair("time" + i, Globals.moveTime[i]));
		}
		
		try {
			//-----[POST送信]
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePair, "utf-8"));
			HttpResponse response = httpclient.execute(httppost);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			response.getEntity().writeTo(byteArrayOutputStream);

		    //-----[サーバーからの応答を取得]
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				System.out.println(byteArrayOutputStream.toString());
		    } else {
		    	System.out.println(byteArrayOutputStream.toString());
		    }
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	private void gameEnd() {
		System.out.println("Game End");
		gameEnded = true;
		Date date = new Date();
		Globals.endTime = String.valueOf(DateFormat.format(Globals.datePattern, date));
		for(int i = 0; i < Globals.evaluationValue.length; i++) {
			Globals.evaluationValue[i] = String.valueOf(evaluations[evaluations.length - i - 2]);
		}
	}

}
