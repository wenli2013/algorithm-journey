package class180;

// 消耗战，C++版
// 一共有n个节点，给定n-1条无向边，每条边有边权，所有节点组成一棵树
// 一共有q条查询，每条查询格式如下
// 查询 k a1 a2 ... ak : 给出了k个不同的关键节点，并且一定不包含1号节点
//                       你可以随意选择边进行切断，切断的代价就是边权
//                       目的是让所有关键点都无法到达1号节点，打印最小总代价
// 1 <= n、q <= 5 * 10^5
// 1 <= 所有查询给出的点的总数 <= 5 * 10^5
// 测试链接 : https://www.luogu.com.cn/problem/P2495
// 如下实现是C++的版本，C++版本和java版本逻辑完全一样
// 提交如下代码，可以通过所有测试用例

//#include <bits/stdc++.h>
//
//using namespace std;
//
//const int MAXN = 500001;
//const int MAXP = 20;
//int n, q, k;
//
//int headg[MAXN];
//int nextg[MAXN << 1];
//int tog[MAXN << 1];
//int weightg[MAXN << 1];
//int cntg;
//
//int headv[MAXN];
//int nextv[MAXN];
//int tov[MAXN];
//int weightv[MAXN];
//int cntv;
//
//int dep[MAXN];
//int dfn[MAXN];
//int stjump[MAXN][MAXP];
//int mindist[MAXN][MAXP];
//int cntd;
//
//int arr[MAXN];
//bool isKey[MAXN];
//int tmp[MAXN << 1];
//int stk[MAXN];
//
//long long cost[MAXN];
//
//bool cmp(int x, int y) {
//    return dfn[x] < dfn[y];
//}
//
//void addEdgeG(int u, int v, int w) {
//    nextg[++cntg] = headg[u];
//    tog[cntg] = v;
//    weightg[cntg] = w;
//    headg[u] = cntg;
//}
//
//void addEdgeV(int u, int v, int w) {
//    nextv[++cntv] = headv[u];
//    tov[cntv] = v;
//    weightv[cntv] = w;
//    headv[u] = cntv;
//}
//
//void dfs(int u, int fa, int w) {
//    dep[u] = dep[fa] + 1;
//    dfn[u] = ++cntd;
//    stjump[u][0] = fa;
//    mindist[u][0] = w;
//    for (int p = 1; p < MAXP; p++) {
//        stjump[u][p] = stjump[stjump[u][p - 1]][p - 1];
//        mindist[u][p] = min(mindist[u][p - 1], mindist[stjump[u][p - 1]][p - 1]);
//    }
//    for (int e = headg[u]; e; e = nextg[e]) {
//        int v = tog[e];
//        if (v != fa) dfs(v, u, weightg[e]);
//    }
//}
//
//int getLca(int a, int b) {
//    if (dep[a] < dep[b]) {
//        swap(a, b);
//    }
//    for (int p = MAXP - 1; p >= 0; p--) {
//        if (dep[stjump[a][p]] >= dep[b]) {
//            a = stjump[a][p];
//        }
//    }
//    if (a == b) {
//        return a;
//    }
//    for (int p = MAXP - 1; p >= 0; p--) {
//        if (stjump[a][p] != stjump[b][p]) {
//            a = stjump[a][p];
//            b = stjump[b][p];
//        }
//    }
//    return stjump[a][0];
//}
//
//int getDist(int u, int v) {
//    int dist = 100000001;
//    for (int p = MAXP - 1; p >= 0; p--) {
//        if (dep[stjump[v][p]] >= dep[u]) {
//            dist = min(dist, mindist[v][p]);
//            v = stjump[v][p];
//        }
//    }
//    return dist;
//}
//
//int buildVirtualTree1() {
//    sort(arr + 1, arr + k + 1, cmp);
//    int len = 0;
//    tmp[++len] = 1;
//    for (int i = 1; i < k; i++) {
//        tmp[++len] = arr[i];
//        tmp[++len] = getLca(arr[i], arr[i + 1]);
//    }
//    tmp[++len] = arr[k];
//    sort(tmp + 1, tmp + len + 1, cmp);
//    int unique = 1;
//    for (int i = 2; i <= len; i++) {
//        if (tmp[unique] != tmp[i]) {
//            tmp[++unique] = tmp[i];
//        }
//    }
//    cntv = 0;
//    for (int i = 1; i <= unique; i++) {
//        headv[tmp[i]] = 0;
//    }
//    for (int i = 1; i < unique; i++) {
//        int lca = getLca(tmp[i], tmp[i + 1]);
//        addEdgeV(lca, tmp[i + 1], getDist(lca, tmp[i + 1]));
//    }
//    return tmp[1];
//}
//
//int buildVirtualTree2() {
//    sort(arr + 1, arr + k + 1, cmp);
//    cntv = 0;
//    headv[1] = 0;
//    int top = 0;
//    stk[++top] = 1;
//    for (int i = 1; i <= k; i++) {
//        int x = arr[i];
//        int y = stk[top];
//        int lca = getLca(x, y);
//        while (top > 1 && dfn[stk[top - 1]] >= dfn[lca]) {
//            addEdgeV(stk[top - 1], stk[top], getDist(stk[top - 1], stk[top]));
//            top--;
//        }
//        if (lca != stk[top]) {
//            headv[lca] = 0;
//            addEdgeV(lca, stk[top], getDist(lca, stk[top]));
//            stk[top] = lca;
//        }
//        headv[x] = 0;
//        stk[++top] = x;
//    }
//    while (top > 1) {
//        addEdgeV(stk[top - 1], stk[top], getDist(stk[top - 1], stk[top]));
//        top--;
//    }
//    return stk[1];
//}
//
//void dp(int u) {
//    for (int e = headv[u]; e; e = nextv[e]) {
//        dp(tov[e]);
//    }
//    cost[u] = 0;
//    for (int e = headv[u]; e; e = nextv[e]) {
//        int v = tov[e];
//        int w = weightv[e];
//        if (isKey[v]) {
//            cost[u] += w;
//        } else {
//            cost[u] += min(cost[v], (long long)w);
//        }
//    }
//}
//
//long long compute() {
//    for (int i = 1; i <= k; i++) {
//        isKey[arr[i]] = true;
//    }
//    int tree = buildVirtualTree1();
//    // int tree = buildVirtualTree2();
//    dp(tree);
//    for (int i = 1; i <= k; i++) {
//        isKey[arr[i]] = false;
//    }
//    return cost[tree];
//}
//
//int main() {
//    ios::sync_with_stdio(false);
//    cin.tie(nullptr);
//    cin >> n;
//    for (int i = 1, u, v, w; i < n; i++) {
//        cin >> u >> v >> w;
//        addEdgeG(u, v, w);
//        addEdgeG(v, u, w);
//    }
//    dfs(1, 0, 0);
//    cin >> q;
//    for (int t = 1; t <= q; t++) {
//        cin >> k;
//        for (int i = 1; i <= k; i++) {
//            cin >> arr[i];
//        }
//        cout << compute() << '\n';
//    }
//    return 0;
//}