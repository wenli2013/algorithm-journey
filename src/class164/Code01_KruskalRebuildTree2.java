package class164;

// Kruskal重构树模版题，C++版
// 测试链接 : https://www.luogu.com.cn/problem/P2245
// 如下实现是C++的版本，C++版本和java版本逻辑完全一样
// 提交如下代码，可以通过所有测试用例

//#include <bits/stdc++.h>
//
//using namespace std;
//
//struct Edge {
//	int u, v, w;
//};
//
//const int MAXN = 200001;
//const int MAXM = 300001;
//int n, m, q;
//Edge arr[MAXM];
//
//int father[MAXN];
//int nodeKey[MAXN];
//int cnth = 0;
//
//int head[MAXN];
//int nxt[MAXN];
//int to[MAXN];
//int cntg = 0;
//
//int fa[MAXN];
//int dep[MAXN];
//int siz[MAXN];
//int son[MAXN];
//int top[MAXN];
//
//bool cmp(Edge x, Edge y) {
//	return x.w < y.w;
//}
//
//int find(int i) {
//	if(i != father[i]) {
//		father[i] = find(father[i]);
//	}
//	return father[i];
//}
//
//void addEdge(int u, int v) {
//    nxt[++cntg] = head[u];
//    to[cntg] = v;
//    head[u] = cntg;
//}
//
//void kruskalRebuild() {
//    for (int i = 1; i <= n; i++) {
//        father[i] = i;
//    }
//    sort(arr + 1, arr + m + 1, cmp);
//    cnth = n;
//    for (int i = 1, fx, fy; i <= m; i++) {
//        fx = find(arr[i].u);
//        fy = find(arr[i].v);
//        if (fx != fy) {
//            father[fx] = father[fy] = ++cnth;
//            father[cnth] = cnth;
//            nodeKey[cnth] = arr[i].w;
//            addEdge(cnth, fx);
//            addEdge(cnth, fy);
//        }
//    }
//}
//
//void dfs1(int u, int f) {
//    fa[u] = f;
//    dep[u] = dep[f] + 1;
//    siz[u] = 1;
//    for (int e = head[u], v; e > 0; e = nxt[e]) {
//        v = to[e];
//        if (v != f) {
//            dfs1(v, u);
//        }
//    }
//    for (int e = head[u], v; e > 0; e = nxt[e]) {
//        v = to[e];
//        if (v != f) {
//            siz[u] += siz[v];
//            if (son[u] == 0 || siz[son[u]] < siz[v]) {
//                son[u] = v;
//            }
//        }
//    }
//}
//
//void dfs2(int u, int t) {
//    top[u] = t;
//    if (son[u] == 0) {
//        return;
//    }
//    dfs2(son[u], t);
//    for (int e = head[u], v; e > 0; e = nxt[e]) {
//        v = to[e];
//        if (v != fa[u] && v != son[u]) {
//            dfs2(v, v);
//        }
//    }
//}
//
//int lca(int a, int b) {
//    while (top[a] != top[b]) {
//        if (dep[top[a]] <= dep[top[b]]) {
//            b = fa[top[b]];
//        } else {
//            a = fa[top[a]];
//        }
//    }
//    return dep[a] <= dep[b] ? a : b;
//}
//
//int main() {
//    ios::sync_with_stdio(false);
//    cin.tie(nullptr);
//    cin >> n >> m;
//    for (int i = 1; i <= m; i++) {
//        cin >> arr[i].u >> arr[i].v >> arr[i].w;
//    }
//    kruskalRebuild();
//    for (int i = 1; i <= cnth; i++) {
//        if (i == father[i]) {
//            dfs1(i, 0);
//            dfs2(i, i);
//        }
//    }
//    cin >> q;
//    for (int i = 1, x, y; i <= q; i++) {
//        cin >> x >> y;
//        if (find(x) != find(y)) {
//            cout << "impossible\n";
//        } else {
//            cout << nodeKey[lca(x, y)] << "\n";
//        }
//    }
//    return 0;
//}