// Version 3.2
// Uses a simplified miniMax rather than alphaBeta in an effort to try to 
//weed
// out bugs.
// Still some awkward behaviors near end-game. Not quite sure what causes
// them.
import java.util.LinkedList;
public class MiniMaxAI extends AIModule
{
public final int MAX_PLAYER = 1;
public final int MIN_PLAYER = 2;
public final double SCORE_MAX = Double.MAX_VALUE;
public final double SCORE_MIN = Double.MIN_VALUE;
public final double SCORE_4_INAROW = 1000000000;
public final double SCORE_3_INAROW = 24;
public final double SCORE_2_INAROW = 8;
public final double SCORE_1_INAROW = 1;
// Exception used to exit out the recursive chain when the terminate 
//signal has been set
public class TerminationException extends RuntimeException {
private static final long serialVersionUID = 
2302284536348239006L;
};
public String scoreToString(double score) {
if (score <= SCORE_MIN) return "MIN";
if (score >= SCORE_MAX) return "MAX";
if (score <= -SCORE_4_INAROW) return "LOSE";
if (score >= SCORE_4_INAROW) return "WIN";
return "" + score;
}
public boolean onboard(final int x, final int y, final int width, 
final int height) {
if (x < 0) return false;
if (y < 0) return false;
if (x >= width) return false;
if (y >= height) return false;
return true;
}
public double check(final GameStateModule state, final int player, 
final int opponent, int x, int y, int dx, int dy) {
final int width = state.getWidth();
final int middle = width / 2;
final int height = state.getHeight();
final int empty = 0;
double score = 0.0;
int ends;
if (onboard(x, y, width, height) && state.getAt(x, y) == player) 
{
x += dx;
y += dy;
if (onboard(x, y, width, height) && state.getAt(x, y) == 
player) {
x += dx;
y += dy;
if (onboard(x, y, width, height) && state.getAt(x, y)
== player) {
x += dx;
y += dy;
if (onboard(x, y, width, height) && 
state.getAt(x, y) == player) {
return SCORE_4_INAROW;
}
ends = 0;
if (onboard(x, y, width, height) && 
state.getAt(x, y) == empty) ends ++;
if (onboard(x - 4 * dx, y - 4 * dy, width, 
height) && state.getAt(x - 4 * dx, y - 4 * dy) == empty) ends ++;
if (ends == 2) {
return SCORE_4_INAROW;
}
score += SCORE_3_INAROW * ends;
if (dy == 0 && dx == 1 && y % 2 == 0) 
// Prioritize odd threats
score += SCORE_3_INAROW * ends;
y -= dy;
x -= dx;
}
ends = 0;
if (onboard(x, y, width, height) && state.getAt(x, y)
== empty) ends ++;
if (onboard(x - 3 * dx, y - 3 * dy, width, height) 
&& state.getAt(x - 3 * dx, y - 3 * dy) == empty) ends ++;
score += SCORE_2_INAROW * ends;
y -= dy;
x -= dx;
}
ends = 0;
if (onboard(x, y, width, height) && state.getAt(x, y) == 
empty) ends ++;
if (onboard(x - 2 * dx, y - 2 * dy, width, height) && 
state.getAt(x - 2 * dx, y - 2 * dy) == empty) ends ++;
score += SCORE_1_INAROW / (Math.abs(x - middle) + 1) * 
ends;
y -= dy;
x -= dx;
}
if (onboard(x, y, width, height) && state.getAt(x, y) == 
opponent) {
x += dx;
y += dy;
if (onboard(x, y, width, height) && state.getAt(x, y) == 
opponent) {
x += dx;
y += dy;
if (onboard(x, y, width, height) && state.getAt(x, y)
== opponent) {
x += dx;
y += dy;
if (onboard(x, y, width, height) && 
state.getAt(x, y) == opponent) {
return -SCORE_4_INAROW;
}
ends = 0;
if (onboard(x, y, width, height) && 
state.getAt(x, y) == empty) ends ++;
if (onboard(x - 4 * dx, y - 4 * dy, width, 
height) && state.getAt(x - 4 * dx, y - 4 * dy) == empty) ends ++;
if (ends == 2) {
return -SCORE_4_INAROW;
}
score -= SCORE_3_INAROW * ends;
if (dy == 0 && dx == 1 && y % 2 == 0) 
// Prioritize odd threats
score -= SCORE_3_INAROW * ends;
y -= dy;
x -= dx;
}
ends = 0;
if (onboard(x, y, width, height) && state.getAt(x, y)
== empty) ends ++;
if (onboard(x - 3 * dx, y - 3 * dy, width, height) 
&& state.getAt(x - 3 * dx, y - 3 * dy) == empty) ends ++;
score -= SCORE_2_INAROW * ends;
y -= dy;
x -= dx;
}
ends = 0;
if (onboard(x, y, width, height) && state.getAt(x, y) == 
empty) ends ++;
if (onboard(x - 2 * dx, y - 2 * dy, width, height) && 
state.getAt(x - 2 * dx, y - 2 * dy) == empty) ends ++;
score -= SCORE_1_INAROW / (Math.abs(x - middle) + 1) * 
ends;
y -= dy;
x -= dx;
}
return score;
}
// Static evaluation / heuristic function to judge a given state of 
//the board
public double heuristic(final GameStateModule state, final int player) 
{
final int good = player;
final int bad = player % 2 + 1;
double score = 0.0;
for (int x = 0; x < state.getWidth(); x ++) {
for (int y = 0; y < state.getHeight(); y ++) {
//System.out.print("Checking (" + x + "," + y + "):");
double horizontal = check(state, good, bad, x, y, 1, 0);
//System.out.print(" H=" + horizontal);
if (horizontal <= -SCORE_4_INAROW || horizontal >= 
SCORE_4_INAROW) return horizontal;
double vertical = check(state, good, bad, x, y, 0, 
1);
//System.out.print(" V=" + vertical);
if (vertical <= -SCORE_4_INAROW || vertical >= 
SCORE_4_INAROW) return vertical;
double diagonalDR = check(state, good, bad, x, y, 1, 
-1);
//System.out.print(" DR=" + diagonalDR);
if (diagonalDR <= -SCORE_4_INAROW || diagonalDR >= 
SCORE_4_INAROW) return diagonalDR;
double diagonalDL = check(state, good, bad, x, y, -1, -1);
//System.out.print(" DL=" + diagonalDL);
if (diagonalDL <= -SCORE_4_INAROW || diagonalDL >= 
SCORE_4_INAROW) return diagonalDL;
double diagonalUR = check(state, good, bad, x, y, 1, 
1);
//System.out.print(" UR=" + diagonalUR);
if (diagonalUR <= -SCORE_4_INAROW || diagonalUR >= 
SCORE_4_INAROW) return diagonalUR;
double diagonalUL = check(state, good, bad, x, y, -1, 1);
//System.out.print(" UL=" + diagonalUL);
if (diagonalUL <= -SCORE_4_INAROW || diagonalUL >= 
SCORE_4_INAROW) return diagonalUL;
//System.out.println();
score += horizontal + vertical + diagonalDR + 
diagonalDL + diagonalUR + diagonalUL;
}
}
return score;
}
// Recursive miniMax algorithm
public double miniMax(final GameStateModule state, final int player, 
final int depth, boolean root) {
// Check for termination
if (terminate) throw new TerminationException();
// If game is over
double heuristic = heuristic(state, player);
if (Math.abs(heuristic) >= SCORE_4_INAROW) return heuristic;
// Check if at maximum search depth
if (depth == 0) return heuristic;
// Get possible moves - reorder this later to optimize the alphabeta pruning
LinkedList<Integer> moves = new LinkedList<Integer>();
for (int move = 0; move < state.getWidth(); move ++)
if (state.canMakeMove(move))
moves.add(move);
// Check if leaf
if (moves.size() == 0) return heuristic;
int activePlayer = state.getActivePlayer();
boolean maxTurn = activePlayer == MAX_PLAYER;
double best = maxTurn ? Double.MIN_VALUE : Double.MAX_VALUE;
for (int move : moves) {
state.makeMove(move);
double result = miniMax(state, player, depth - 1, false);
state.unMakeMove();
if ((maxTurn && result > best) || (!maxTurn && result < 
best)) {
best = result;
if (root) chosenMove = move;
}
}
return best;
}
// Default entry-point for the alpha beta pruned miniMax algorithm
public double alphaBeta(final GameStateModule state, final int player, 
final int depth) {
return alphaBeta(state, player, depth, true, Double.MIN_VALUE, 
Double.MAX_VALUE);
}
// Recursive miniMax algorithm
public double alphaBeta(final GameStateModule state, final int player, 
final int depth, boolean root, double alpha, double beta) {
// Check for termination
if (terminate) throw new TerminationException();
// If game is over
double heuristic = heuristic(state, player);
if (Math.abs(heuristic) >= SCORE_4_INAROW) return heuristic;
// Check if at maximum search depth
if (depth == 0) return heuristic;
// Get possible moves - reorder this later to optimize the alphabeta pruning
LinkedList<Integer> moves = new LinkedList<Integer>();
for (int move = 0; move < state.getWidth(); move ++)
if (state.canMakeMove(move))
moves.add(move);
// Check if leaf
if (moves.size() == 0) return heuristic;
// If MAX's turn to move
if (state.getActivePlayer() == MAX_PLAYER) {
for (int move : moves) {
state.makeMove(move);
double result = alphaBeta(state, player, depth - 1, 
false, alpha, beta);
state.unMakeMove();
if (root) System.out.print("[" + move + "," + 
scoreToString(result) + "] ");
if (result > alpha) {
alpha = result;
if (root) chosenMove = move;
}
if (alpha >= beta) return alpha;
}
if (root && alpha == Double.MIN_VALUE) {
System.out.println();
System.out.println("FOUND BAD SPOT! moves.size() = " 
+ moves.size() + ", alpha-beta: (" + alpha + "," + beta + ")");
}
return alpha;
}
// If MIN's turn to move
if (state.getActivePlayer() == MIN_PLAYER) {
for (int move : moves) {
state.makeMove(move);
double result = alphaBeta(state, player, depth - 1, 
false, alpha, beta);
state.unMakeMove();
if (result < beta) {
beta = result;
if (root) chosenMove = move;
}
if (beta <= alpha) return beta;
}
return beta;
}
// Default Return
return heuristic;
}
@Override
public void getNextMove(final GameStateModule state)
{
// Choose default move
chosenMove = (int)(state.getWidth() / 2);
// Get player number
final int player = state.getActivePlayer();
// Run iterative deepening minimax search
int depth = 1;
while (depth < 50) {
System.out.print("Running minimax to depth " + depth 
+ ". ");
System.out.print("Current heuristic is " + 
heuristic(state, player) + ". ");
try {
miniMax(state, player, depth, true);
System.out.println("Best move found is " + 
chosenMove + ".");
} catch (TerminationException e) {
System.out.println("Told to terminate.");
return;
}
depth += 1;
}
}
}