package class164;

// 归程，java实现迭代版
// 测试链接 : https://www.luogu.com.cn/problem/P4768
// 提交以下的code，提交时请把类名改成"Main"，可以通过所有测试用例

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.PriorityQueue;

public class Code02_ReturnJourney2 {

	public static int MAXN = 200001;
	public static int MAXK = 400001;
	public static int MAXM = 400001;
	public static int MAXH = 20;
	public static int INF = 2000000001;
	public static int t, n, m, q, k, s;
	public static int[][] arr = new int[MAXM][4];

	public static int[] headg = new int[MAXN];
	public static int[] nextg = new int[MAXM << 1];
	public static int[] tog = new int[MAXM << 1];
	public static int[] weightg = new int[MAXM << 1];
	public static int cntg;

	public static int[] dist = new int[MAXN];
	public static boolean[] visit = new boolean[MAXN];
	public static PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> a[1] - b[1]);

	public static int[] headk = new int[MAXK];
	public static int[] nextk = new int[MAXK];
	public static int[] tok = new int[MAXK];
	public static int cntk;

	public static int[] father = new int[MAXK];
	public static int[] nodeKey = new int[MAXK];
	public static int[] stack = new int[MAXK];
	public static int cntu;

	public static int[] dep = new int[MAXK];
	public static int[][] stjump = new int[MAXK][MAXH];
	public static int[] mindist = new int[MAXK];

	public static void prepare() {
		cntg = cntk = 0;
		Arrays.fill(headg, 1, n + 1, 0);
		Arrays.fill(headk, 1, n * 2, 0);
	}

	public static void addEdgeG(int u, int v, int w) {
		nextg[++cntg] = headg[u];
		tog[cntg] = v;
		weightg[cntg] = w;
		headg[u] = cntg;
	}

	public static void dijkstra() {
		for (int i = 1; i <= m; i++) {
			addEdgeG(arr[i][0], arr[i][1], arr[i][2]);
			addEdgeG(arr[i][1], arr[i][0], arr[i][2]);
		}
		Arrays.fill(dist, 1, n + 1, INF);
		Arrays.fill(visit, 1, n + 1, false);
		dist[1] = 0;
		heap.add(new int[] { 1, 0 });
		int[] cur;
		int x, v;
		while (!heap.isEmpty()) {
			cur = heap.poll();
			x = cur[0];
			v = cur[1];
			if (!visit[x]) {
				visit[x] = true;
				for (int e = headg[x], y, w; e > 0; e = nextg[e]) {
					y = tog[e];
					w = weightg[e];
					if (!visit[y] && dist[y] > v + w) {
						dist[y] = v + w;
						heap.add(new int[] { y, dist[y] });
					}
				}
			}
		}
	}

	public static void addEdgeK(int u, int v) {
		nextk[++cntk] = headk[u];
		tok[cntk] = v;
		headk[u] = cntk;
	}

	public static int find(int i) {
		int size = 0;
		while (i != father[i]) {
			stack[size++] = i;
			i = father[i];
		}
		while (size > 0) {
			father[stack[--size]] = i;
		}
		return i;
	}

	public static void kruskalRebuild() {
		for (int i = 1; i <= n; i++) {
			father[i] = i;
		}
		Arrays.sort(arr, 1, m + 1, (a, b) -> b[3] - a[3]);
		cntu = n;
		for (int i = 1, fx, fy; i <= m; i++) {
			fx = find(arr[i][0]);
			fy = find(arr[i][1]);
			if (fx != fy) {
				father[fx] = father[fy] = ++cntu;
				father[cntu] = cntu;
				nodeKey[cntu] = arr[i][3];
				addEdgeK(cntu, fx);
				addEdgeK(cntu, fy);
			}
		}
	}

	public static int[][] fse = new int[MAXK][3];

	public static int stacksize, first, second, edge;

	public static void push(int fir, int sec, int edg) {
		fse[stacksize][0] = fir;
		fse[stacksize][1] = sec;
		fse[stacksize][2] = edg;
		stacksize++;
	}

	public static void pop() {
		--stacksize;
		first = fse[stacksize][0];
		second = fse[stacksize][1];
		edge = fse[stacksize][2];
	}

	public static void dfs(int u, int fa) {
		stacksize = 0;
		push(u, fa, -1);
		while (stacksize > 0) {
			pop();
			if (edge == -1) {
				dep[first] = dep[second] + 1;
				stjump[first][0] = second;
				for (int p = 1; p < MAXH; p++) {
					stjump[first][p] = stjump[stjump[first][p - 1]][p - 1];
				}
				edge = headk[first];
			} else {
				edge = nextk[edge];
			}
			if (edge != 0) {
				push(first, second, edge);
				push(tok[edge], first, -1);
			} else {
				if (first <= n) {
					mindist[first] = dist[first];
				} else {
					mindist[first] = INF;
				}
				for (int e = headk[first]; e > 0; e = nextk[e]) {
					mindist[first] = Math.min(mindist[first], mindist[tok[e]]);
				}
			}
		}
	}

	public static int query(int node, int line) {
		for (int p = MAXH - 1; p >= 0; p--) {
			if (stjump[node][p] > 0 && nodeKey[stjump[node][p]] > line) {
				node = stjump[node][p];
			}
		}
		return mindist[node];
	}

	public static void main(String[] args) {
		FastIO io = new FastIO(System.in, System.out);
		t = io.nextInt();
		for (int test = 1; test <= t; test++) {
			n = io.nextInt();
			m = io.nextInt();
			prepare();
			for (int i = 1; i <= m; i++) {
				arr[i][0] = io.nextInt();
				arr[i][1] = io.nextInt();
				arr[i][2] = io.nextInt();
				arr[i][3] = io.nextInt();
			}
			dijkstra();
			kruskalRebuild();
			dfs(cntu, 0);
			q = io.nextInt();
			k = io.nextInt();
			s = io.nextInt();
			for (int i = 1, node, line, lastAns = 0; i <= q; i++) {
				node = (io.nextInt() + k * lastAns - 1) % n + 1;
				line = (io.nextInt() + k * lastAns) % (s + 1);
				lastAns = query(node, line);
				io.writelnInt(lastAns);
			}
		}
		io.flush();
	}

	// 读写工具类
	static class FastIO {
		private final InputStream is;
		private final OutputStream os;
		private final byte[] inbuf = new byte[1 << 16];
		private int lenbuf = 0;
		private int ptrbuf = 0;
		private final StringBuilder outBuf = new StringBuilder();

		public FastIO(InputStream is, OutputStream os) {
			this.is = is;
			this.os = os;
		}

		private int readByte() {
			if (ptrbuf >= lenbuf) {
				ptrbuf = 0;
				try {
					lenbuf = is.read(inbuf);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				if (lenbuf == -1) {
					return -1;
				}
			}
			return inbuf[ptrbuf++] & 0xff;
		}

		private int skip() {
			int b;
			while ((b = readByte()) != -1) {
				if (b > ' ') {
					return b;
				}
			}
			return -1;
		}

		public int nextInt() {
			int b = skip();
			if (b == -1) {
				throw new RuntimeException("No more integers (EOF)");
			}
			boolean negative = false;
			if (b == '-') {
				negative = true;
				b = readByte();
			}
			int val = 0;
			while (b >= '0' && b <= '9') {
				val = val * 10 + (b - '0');
				b = readByte();
			}
			return negative ? -val : val;
		}

		public void write(String s) {
			outBuf.append(s);
		}

		public void writeInt(int x) {
			outBuf.append(x);
		}

		public void writelnInt(int x) {
			outBuf.append(x).append('\n');
		}

		public void flush() {
			try {
				os.write(outBuf.toString().getBytes());
				os.flush();
				outBuf.setLength(0);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
