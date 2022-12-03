import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Arrays;
import java.util.ArrayList;

class KeyNodeArr{ // splitNode 함수에서 key와 TreeNode를 한번에 return하기위해 사용할 class
	int key = 0;
	TreeNode node = null;
	
	KeyNodeArr(int key, TreeNode t){
		this.key = key;
		this.node = t;
	}
}

class TreeNode{
	int n; // n: 해당 노드가 갖고 있는 키의 개수
	int[] K = null; // 해당 노드가 갖고 있는 키
	TreeNode[] P = null; // 해당 노드의 자식노드
	
	TreeNode(int m){
		this.n = 0;
		this.K = new int[m]; // insertKey를 위해 필요한 크기보다 1 크게 만듦
		this.P = new TreeNode[m+1]; // insertKey를 위해 필요한 크기보다 1 크게 만듦 
	}
	
	TreeNode(int n, int[] k, TreeNode[] p) {
		this.n = n;
		this.K = k;
		this.P = p;
	}
}

public class BT {
	TreeNode root = null; // root node
	Deque<TreeNode> stack = new ArrayDeque<>(); // stack
	
	// node를 찾아 발견 여부를 return하고 경로를 stack에 저장
	boolean searchPath(TreeNode T, int m, int key) { // m: 트리의 차원, key: 찾을 key	
		TreeNode x = T;
		int i = 0;
		do {
			i = 0;
			while(i < x.n && key > x.K[i]) i += 1;
			
			// 삽입할 키 발견했을 경우 -> 이미 트리에 존재하므로 삽입 불가
			stack.push(x); // 경로 update
			if(i < x.n && key == x.K[i]) return true; // 종료
			
			// 삽입할 키를 아직 발견하지 못했을 경우
		} while((x = x.P[i]) != null);
		return false;
	}
	
	// node x의 적당한 위치에 삽입
	void insertKey(int m, TreeNode x, TreeNode y, int newKey) { // y: 자식 노드에서 split되어 새로 만들어진 노드
		// newKey보다 큰 key들을 오른쪽으로 한 칸씩 이동
		int i = x.n - 1;
		
		while(i >= 0 && newKey < x.K[i]) {
			x.K[i+1] = x.K[i];
			x.P[i+2] = x.P[i+1];
			i -= 1;
		}
		
		// newKey 삽입
		x.K[i+1] = newKey;
		x.P[i+2] = y;
		x.n += 1;
	}
	
	void copyNode(TreeNode a, TreeNode b, int m) { // copy a to b
		b.n = a.n;
		for(int i = 0; i < m; i++) {
			b.K[i] = a.K[i];
			b.P[i] = a.P[i];
		}
	}
	
	void clearNode(TreeNode t, int m) { // node의 n, K, P를 모두 초기화
		t.n = 0;
		for(int i=0; i<m; i++) {
			t.K[i] = 0;
			t.P[i] = null;
		}
	}
	
	// split 수행
	KeyNodeArr splitNode(int m, TreeNode x, TreeNode y, int newKey) {
		TreeNode tmpNode = new TreeNode(m+1);
		copyNode(x, tmpNode, m); // overflow를 위한 임시 노드
		insertKey(m, tmpNode, y, newKey);
		
		int center = tmpNode.n/2;
		int centerKey = tmpNode.K[center]; // 분할 기준
		
		// centerKey 이전 값을 노드 x로 복사
		clearNode(x, m);
		int i = 0;
		
		while(tmpNode.K[i] < centerKey) {
			x.K[i] = tmpNode.K[i];
			x.P[i] = tmpNode.P[i];
			i += 1;
			x.n += 1;
		}
		x.P[i] = tmpNode.P[i];
		
		TreeNode newNode = new TreeNode(m); // centerKey 이후 값을 노드 newNode로 복사
		i += 1; // centerKey 값 건너뜀
		while(i < tmpNode.n) {
			newNode.K[newNode.n] = tmpNode.K[i];
			newNode.P[newNode.n] = tmpNode.P[i];
			i += 1;
			newNode.n += 1;
		}
		newNode.P[newNode.n] = tmpNode.P[i];
		
		return new KeyNodeArr(centerKey, newNode);
	}
	
	// oldKey를 x에서 제거
	void deleteKey(int m, TreeNode x, int oldKey) {
		// oldKey의 위치 i 탐색
		int i = 0;
		while(oldKey > x.K[i]) i += 1;
		
		// oldKey보다 큰 key들을 왼쪽으로 한 칸씩 이동, 현재 i는 oldKey 위치
		while(i < x.n) {
			x.K[i] = x.K[i+1];
			x.P[i+1] = x.P[i+2];
			i += 1;
		}
		x.n -= 1;
	}
	
	// x의 best 형제 노드 위치 반환
	int bestSibling(int m, TreeNode x, TreeNode y) { // y: x의 부모 노드
		// y에서 x의 위치 i를 탐색
		int i = 0;
		while(y.P[i] != x) i += 1;
		
		// 바로 인접한 두 형제 중, 키의 개수가 많은 형제를 bestSibling으로 선택
		if(i == 0) return i+1; // 왼쪽 형제 없음
		else if(i == y.n) return i-1; // 오른쪽 형제 없음
		else if(y.P[i].n >= y.P[i+1].n) return i-1;
		else return i+1;
	}
	
	// x와 best sibling 노드 간의 키 재분배 수행
	void redistributeKeys(int m, TreeNode x, TreeNode y, int bs) { // y: x의 부모 노드, bs: best sibling index
		// y에서 x의 위치 i를 탐색
		int i = 0;
		while(y.P[i] != x) i += 1;
		
		TreeNode bestNode = y.P[bs];
		if(bs < i) {
			int lastKey = bestNode.K[bestNode.n-1];
			insertKey(m, x, null, y.K[i-1]);
			x.P[1] = x.P[0];
			x.P[0] = bestNode.P[bestNode.n];
			bestNode.P[bestNode.n] = null;
			deleteKey(m, bestNode, lastKey);
			y.K[i-1] = lastKey;
		} else {
			int firstKey = bestNode.K[0];
			insertKey(m, x, null, y.K[i]);
			x.P[x.n] = bestNode.P[0];
		    bestNode.P[0] = bestNode.P[1];
			deleteKey(m, bestNode, firstKey);
			y.K[i] = firstKey;
		}
	}
	
	// x와 best sibling 노드 간의 합병 수행
	void mergeNode(int m, TreeNode x, TreeNode y, int bs) {
		int i = 0; // y에서 x의 위치 i탐색
		while(y.P[i] != x) i += 1;
		
		TreeNode bestNode = y.P[bs];
		// 왼쪽 형제 노드로의 병합만 고려할 수 있도록 swap
		if(bs > i) {
			int tmp = i;
			i = bs;
			bs = tmp;
			
			TreeNode tmpNode = x;
			x = bestNode;
			bestNode = tmpNode;
		}
		// 왼쪽 형제 노드와 병합
		bestNode.K[bestNode.n] = y.K[i-1];
		bestNode.n += 1;
		int j = 0;
		while(j < x.n) {
			bestNode.K[bestNode.n] = x.K[j];
			bestNode.P[bestNode.n] = x.P[j];
			bestNode.n += 1;
			j += 1;
		}
		bestNode.P[bestNode.n] = x.P[x.n];
		deleteKey(m, y, y.K[i-1]);
	}
	
	// 노드 삽입
	void insertBT(int m, int newKey) { // m: 트리의 차원, newKey: 새로운 노드의 키
		// root node 생성
		if(this.root == null) {
			this.root = new TreeNode(m);
			this.root.K[0] = newKey;
			this.root.n = 1;
			return;
		}
		
		// newKey를 삽입할 노드의 경로를 탐색하며 스택에 경로 저장
		stack.clear();
		boolean found = searchPath(this.root, m, newKey);
		if(found) {
			System.out.println(String.format("i %d : The key already exists.", newKey));
			return; // 이미 newKey가 존재하는 경우
		}
		
		// newKey가 존재하지 않는 경우, 삽입 가능
		boolean finished = false;
		
		TreeNode x = stack.pop(); // 경로의 top
		TreeNode y = null; // 새로 분할된 노드를 담을 변수
		
		do {
			if(x.n < m-1) { // overflow 발생 여부
				// overflow 발생 안함, newKey 삽입
				insertKey(m, x, y, newKey);
				finished = true;
			} else { // overflow 발생
				// x를 newKey를 기준으로 분할, 분할된 노드 반환
				KeyNodeArr ans = splitNode(m, x, y, newKey);
				newKey = ans.key;
				y = ans.node;
				if(!stack.isEmpty()) x = stack.pop();
				else { // tree level 1 증가
					this.root = new TreeNode(m);
					this.root.K[0] = newKey;
					this.root.P[0] = x;
					this.root.P[1] = y;
					this.root.n = 1;
					finished = true;
				}
			}
		} while(!finished);
	}
	
	boolean isTerminalNode(TreeNode t, int m) {
		for(int i=0; i<m+1; i++) {
			if(t.P[i] != null) return false;
		}
		return true;
	}
	
	// 노드 삭제
	void deleteBT(int m, int oldKey) {
		// oldKey가 있던 노드의 경로 탐색, 스택에 경로 저장
		stack.clear();
		boolean found = searchPath(this.root, m, oldKey);
		if(!found) {
			System.out.println(String.format("d %d : The key does not exist.", oldKey));
			return; // oldKey 발견 못함, 삭제 불가
		}
		
		TreeNode x = stack.pop();
		TreeNode y = null;
		
		
		if(!isTerminalNode(x, m)) { // oldKey를 내부 노드에서 발견
			TreeNode internalNode = x;
			int i = 0;
			while(x.K[i] != oldKey) i++;
			
			stack.push(x);
			
			// 후행키의 위치 탐색 
			searchPath(x.P[i + 1], m, x.K[i]);
			
			// 후행키와 oldKey 교환
			x = stack.pop(); // x = 후행키가 있는 단말 노드
			int tmp = internalNode.K[i];
			internalNode.K[i] = x.K[0];
			x.K[0] = tmp; // x.K[0] = oldKey
		}
		boolean finished = false;
		deleteKey(m, x, oldKey); // 노드 x에서 oldKey 삭제
		
		if(!stack.isEmpty()) y = stack.pop(); // y는 x의 부모 노드
		
		do {
			if(this.root == x || x.n >= (m - 1) / 2) finished = true; // underflow 발생 x
			else { // underflow 발생
				// 키 재분배 또는 노드 합병을 위한 형제 노드 결정
				int bs = bestSibling(m, x, y);
				
				if(y.P[bs].n > (m - 1) / 2) { // bestSibling에서 재분배
					redistributeKeys(m, x, y, bs);
					finished = true;
				} else { // bestSibling과 노드 합병
					mergeNode(m, x, y, bs);
					x = y;
					if(!stack.isEmpty()) y = stack.pop();
					else finished = true;	
				}
			}
		} while(!finished);
		
		if(y != null && y.n == 0) { // y에 key가 없을 경우(비어있는 경우)
			this.root = y.P[0];
		}
	}
	
	// inorder 순회 알고리즘
	void inorderBT(TreeNode T, int m) {
		if(T != null && T.n != 0)
			for(int i=0; i < m; i++) {
				inorderBT(T.P[i], m);
				if(i < T.n)
					System.out.print(T.K[i] + " ");
			}
	}
	
	public static void main(String[] args) throws IOException {
		BT test3 = new BT();
		BT test4 = new BT();
		
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\alina\\Desktop\\Java\\File Processing\\src\\BT-input.txt"));
		
		while(true) {
			String line = br.readLine();
			if(line == null) break;
			String[] llist = line.split(" ");
			
			if(llist[0].equals("i")) 
				test3.insertBT(3, Integer.parseInt(llist[1]));
			else
				test3.deleteBT(3, Integer.parseInt(llist[1]));
			test3.inorderBT(test3.root, 3);
			System.out.println();
		}
		
		br = new BufferedReader(new FileReader("C:\\Users\\alina\\Desktop\\Java\\File Processing\\src\\BT-input.txt"));
		
		while(true) {
			String line = br.readLine();
			if(line == null) break;
			String[] llist = line.split(" ");
			
			if(llist[0].equals("i")) 
				test4.insertBT(4, Integer.parseInt(llist[1]));
			else
				test4.deleteBT(4, Integer.parseInt(llist[1]));
			test4.inorderBT(test4.root, 4);
			System.out.println();
		}
	}
}
