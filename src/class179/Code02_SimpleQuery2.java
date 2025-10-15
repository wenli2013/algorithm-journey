package class179;

// 简单的询问，C++版
// 给定一个长度为n的数组arr，下标从1到n
// 函数get(l, r, x) = arr[l..r]范围上，数组x出现的次数
// 接下来有q条查询，格式如下
// 查询 l1 r1 l2 r2 : 每种x都算，打印 get(l1, r1, x) * get(l2, r2, x) 的累加和
// 1 <= n、q <= 5 * 10^4
// 1 <= arr[i] <= n
// 测试链接 : https://www.luogu.com.cn/problem/P5268
// 如下实现是C++的版本，C++版本和java版本逻辑完全一样
// 提交如下代码，可以通过所有测试用例

//#include <bits/stdc++.h>
//
//using namespace std;
//
//struct Query {
//    int siz1, siz2, op, id;
//};
//
//const int MAXN = 50001;
//int n, q, cntq;
//int arr[MAXN];
//Query query[MAXN << 2];
//int bi[MAXN];
//
//int cnt1[MAXN];
//int cnt2[MAXN];
//long long curAns = 0;
//long long ans[MAXN];
//
//bool QueryCmp(Query &a, Query &b) {
//    if (bi[a.siz1] != bi[b.siz1]) {
//        return bi[a.siz1] < bi[b.siz1];
//    }
//    if (bi[a.siz1] & 1) {
//        return a.siz2 < b.siz2;
//    } else {
//        return a.siz2 > b.siz2;
//    }
//}
//
//void addQuery(int siz1, int siz2, int op, int id) {
//    query[++cntq].siz1 = siz1;
//    query[cntq].siz2 = siz2;
//    query[cntq].op = op;
//    query[cntq].id = id;
//}
//
//void compute() {
//    int win1 = 0, win2 = 0;
//    for (int i = 1; i <= cntq; i++) {
//        int job1 = query[i].siz1;
//        int job2 = query[i].siz2;
//        int op = query[i].op;
//        int id = query[i].id;
//        while (win1 < job1) {
//            win1++;
//            cnt1[arr[win1]]++;
//            curAns += cnt2[arr[win1]];
//        }
//        while (win1 > job1) {
//            cnt1[arr[win1]]--;
//            curAns -= cnt2[arr[win1]];
//            win1--;
//        }
//        while (win2 < job2) {
//            win2++;
//            cnt2[arr[win2]]++;
//            curAns += cnt1[arr[win2]];
//        }
//        while (win2 > job2) {
//            cnt2[arr[win2]]--;
//            curAns -= cnt1[arr[win2]];
//            win2--;
//        }
//        ans[id] += curAns * op;
//    }
//}
//
//void prepare() {
//    int blen = (int)sqrt(n);
//    for (int i = 1; i <= n; i++) {
//        bi[i] = (i - 1) / blen + 1;
//    }
//    for (int i = 1; i <= cntq; i++) {
//        if (query[i].siz1 > query[i].siz2) {
//            swap(query[i].siz1, query[i].siz2);
//        }
//    }
//    sort(query + 1, query + cntq + 1, QueryCmp);
//}
//
//int main() {
//    ios::sync_with_stdio(false);
//    cin.tie(nullptr);
//    cin >> n;
//    for (int i = 1; i <= n; i++) {
//        cin >> arr[i];
//    }
//    cin >> q;
//    for (int i = 1, l1, r1, l2, r2; i <= q; i++) {
//        cin >> l1 >> r1 >> l2 >> r2;
//        addQuery(r1, r2, 1, i);
//        addQuery(r1, l2 - 1, -1, i);
//        addQuery(l1 - 1, r2, -1, i);
//        addQuery(l1 - 1, l2 - 1, 1, i);
//    }
//    prepare();
//    compute();
//    for (int i = 1; i <= q; i++) {
//        cout << ans[i] << '\n';
//    }
//    return 0;
//}