//삽입 : 성공
//삭제 : 성공
// java에서 call by reference하거나 여러 값을 한 번에 return할 수 있는 방법을 찾지 못해 
// changeBalance와 rotateTree에서 쓰이는 p, q, rotationType을 AVL내에 따로 저장할 변수를 두어 사용했습니다.

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

//class TreeNode{ // node 클래스
	Object key;
	int height = 1;
	int bf = 0;
	TreeNode left, right;
	
	TreeNode(TreeNode L, Object key, TreeNode R){
		this.left = L;
		this.right = R;
		this.key = key;
	}
}

public class AVL {
	TreeNode root = getBSTNode(null); // root 노드 
	int cntNodes = 0; // noNode에서 하위노드 수 셀 때 사용
	Deque<TreeNode> stack = new ArrayDeque<>(); // use in noNodes, insertBST
	TreeNode p, q; // p = 불균형이 발생한 노드, q = 불균형이 발생한 노드의 부모 노드
	TreeNode deleteKeyP; // bf 계산을 위해 searchNode로 stack만들때 인자로 줄 변수
	int rotationType; // 0 = NO, 1 = LL, 2 = LR, 3 =RL, 4 = RR
	
	TreeNode getBSTNode(Object key) { // 주어진 key값을 가진 노드 생성
		return new TreeNode(null, key, null);
	}
	
	private int max(int height, int height2) {
		return (height > height2) ? height : height2;
	}
	
	int insertBST(TreeNode T, Object newKey) { // 정상 수행 1, 키 존재하지 않을 때 0
		stack.clear();
		TreeNode q = null;
		TreeNode p = T;
		
		if(T.key == null) {
			T.key = newKey; // tree 처음 생성할 경우
			return 1;
		}
				
		// find position to insert newKey
		while(p != null) {
			if(newKey == p.key) {
				System.out.println(String.format("i %d : The key already exists.", (int)newKey));
				return 0;
			} // 이미 key값을 가진 노드 존재할 경우
			
			q = p;
			stack.push(q);
			
			if((int)newKey < (int)p.key) p = p.left;
			else p = p.right;
		}
		
		// create newNode
		TreeNode newNode = this.getBSTNode(newKey);
		
		// insert newNode as a child of q
		if((int)newKey < (int)q.key) q.left = newNode;
		else q.right = newNode;
		
		// update height
		while(!stack.isEmpty()) {
			q = stack.pop();
			q.height = updateHeight(q); // 함수 사용해서 높이 업데이트	
		}
		return 1;
	}
	
	// key값을 갖는 node 탐색, stack에 경로 저장
	void searchNode(Object key) { // checkBalance에 사용
		if(key == null) return; // key가 null인 경우, 아무 기능을 하지 않고 return
		TreeNode findNode = this.root;
		while(findNode.key != key) { // key값을 갖는 노드를 찾았을 경우 중단
			stack.push(findNode); // stack에 경로 저장
			if(findNode.left == null && findNode.right == null) return; // 찾지 못한 경우, 단말노드에 도달했을 경우 중단
			else if((int)findNode.key < (int)key) findNode = findNode.right; // 찾는 노드가 현재 노드의 오른쪽에 있을 경우
			else findNode = findNode.left; // 찾는 노드가 현재 노드의 왼쪽에 있을 경우
		}
		stack.push(findNode); // 찾은 노드 stack에 push
	}
	
	int updateHeight(TreeNode T) { // 높이 재계산
		int lh = (T.left == null) ? 0 : T.left.height; // left노드가 존재하지 않을 경우 처리
		int rh = (T.right == null) ? 0 : T.right.height; // right노드가 존재하지 않을 경우 처리
		return max(lh, rh) + 1;
	}
	
	int updateBf(TreeNode T) { // balance factor 재계산
		int lh = (T.left == null) ? 0 : T.left.height; // left 노드가 존재하지 않을 경우 처리
		int rh = (T.right == null) ? 0 : T.right.height; // right 노드가 존재하지 않을 경우 처리
		return lh - rh;
	}
	
	// T = this.root로 대체, p, q, rotationType은 클래스 내 변수를 두어 return
	void checkBalance(Object newKey) { // BF 다시 계산하여 불균형 노드(p)와 그의 부모노드(q), rotationType을 설정
		while(!stack.isEmpty()) { // stack에 저장된 경로를 이용해 BF 다시 계산 -> 이 함수 호출 전 stack에 경로 setting해둬야 함(searchNode, minNode, maxNode 등 사용)
			TreeNode n = stack.pop();
			n.bf = updateBf(n); // stack에서 뽑아낸 노드의 bf 재계산
			if(Math.abs(n.bf) > 1) { // 불균형 노드 존재할 경우
				p = n; // 불균형 노드
				if(!stack.isEmpty()) q = stack.pop(); // 부모 노드
				else q = null; // 불균형 노드가 root인 경우
				this.rotationType = findRotationType(p, newKey);
				return;
			}
		} // 불균형 노드가 존재하지 않을 경우
		this.p = null; 
		this.q = null; 
		this.rotationType = 0;
	}
	
	int findRotationType(TreeNode n, Object key) { // rotationType 지정 -> int형으로 지정 후 출력 시 string으로 변환
		int lBf = (n.left == null) ? 0 : n.left.bf; // n.left가 존재하지 않을 때 임의로 값 설정
		int rBf = (n.right == null) ? 0 : n.right.bf; // n.right가 존재하지 않을 때 임의로 값 설정
		
		if(n.bf > 1) {
			if(lBf >= 0) return 1; // LL 
			else return 2; // LR
		}
		if(n.bf < -1) {
			if(rBf <= 0) return 3; // RR
			else return 4; // RL
		}
		else return 0;
	}
	
	// T = this.root로 대체, p,q,rotationType 클래스 내 변수로 저장되어있음.
	void rotateTree() { // 회전시키는 함수
		TreeNode a = this.p;
		TreeNode b = null;
		TreeNode c = null;
		if(this.rotationType == 1) { // LL
			b = a.left;		
			
			a.left = b.right;
			b.right = a;
			
			a.height = updateHeight(a); // 높이 업데이트
			b.height = updateHeight(b);
			
			a.bf = updateBf(a); // bf 재계산
			b.bf = updateBf(b);
		}
		
		else if(this.rotationType == 2) { // LR
			b = p.left;
			c = b.right;	
			
			b.right = c.left;
			a.left = c.right;
			c.left = b;
			c.right = a;
			
			a.height = updateHeight(a);
			b.height = updateHeight(b);
			c.height = updateHeight(c);
			
			a.bf = updateBf(a);
			b.bf = updateBf(b);
			c.bf = updateBf(c);
			
			b = c;
		}
		
		else if(this.rotationType == 3) { // RR
			b = a.right;
			
			a.right = b.left;
			b.left = a;
			
			a.height = updateHeight(a);
			b.height = updateHeight(b);
			
			a.bf = updateBf(a);
			b.bf = updateBf(b);
		}
		
		else { // RL
			b = p.right;
			c = b.left;
			
			b.left = c.right;
			a.right = c.left;
			c.left = a;
			c.right = b;

			a.height = updateHeight(a);
			b.height = updateHeight(b);
			c.height = updateHeight(c);
			
			a.bf = updateBf(a);
			b.bf = updateBf(b);
			c.bf = updateBf(c);
			
			b = c;
		}
		
		if(q == null) { // root 노드가 불균형 노드였을 경우
			this.root = b;	// 회전시킨 값을 root로 재설정
			q = a;
			stack.push(this.root); // bf업데이트를 위해 stack에 push
		}
		else if((int)a.key < (int)this.q.key)// p가 부모노드(q)의 왼쪽에 연결되어있는 경우
			this.q.left = b;
		else // p가 부모노드(q)의 오른쪽에 연결되어있는 경우
			this.q.right = b;
		
		q.height = updateHeight(q); // 부모 노드 높이 재계산
		
		while(true) { // stack에 남아있는 노드 -> 불균형 노드와 그의 부모 노드를 제외한 경로에 해당되는 노드 bf 재계산
			this.q.bf = updateBf(q);
			if(stack.isEmpty()) break;
			this.q = stack.pop();
		}
	}
	
	int insertAVL(Object newKey) { //삽입 실패 == 0 return
		int i = insertBST(this.root, newKey);
		if(i == 0) return i; // 키가 이미 존재할 경우
		searchNode(newKey); // stack 세팅
		checkBalance(newKey); // bf 계산, 불균형 노드 탐색
		if(this.rotationType != 0) rotateTree(); //rotation 실행
		return i;
	}
	
	int height(TreeNode T) { // node 높이
		return T.height;
	}
	
	int noNodes(TreeNode T) { // 하위 노드 개수
		if(T != null) {
			noNodes(T.left);
			cntNodes += 1;
			noNodes(T.right);
		}
		return cntNodes-1;
		
	}
	
	TreeNode maxNode(TreeNode T) { // 하위 노드 중 제일 값이 큰 노드
		TreeNode tmp = T;
		while(tmp.right != null) {
			stack.push(tmp);
			tmp = tmp.right;
		}
		return tmp;
	}
	
	TreeNode minNode(TreeNode T) { // 하위 노드 중 제일 값이 작은 노드
		TreeNode tmp = T;
		while(tmp.left != null) {
			stack.push(tmp);
			tmp = tmp.left;
		}
		return tmp;
	}
	
	int deleteBST(TreeNode T, Object deleteKey) { // 삭제 성공 1, 실패 0
		TreeNode p = T;
		TreeNode q = null;
		stack.clear();
		
		//find position
		while(p != null && !deleteKey.equals(p.key)) {
			q = p;
			stack.push(q);
			
			if((int)deleteKey < (int)p.key) p = p.left;
			else p = p.right;
		}
		
		
		// deleteKey was not found
		if(p == null) {
			deleteKeyP = null;
			System.out.println(String.format("d %d : The key does not exist.", (int)deleteKey));
			return 0;
		}
					
		// case of degree 2
		if(p.left != null && p.right != null) {
			stack.push(p);
			TreeNode tmpNode = p;
			if(height(p.left) <= height(p.right)) 
				p = this.minNode(p.right);
			else if(height(p.left) > height(p.right)) 
				p = this.maxNode(p.left);
			else {
				if(noNodes(p.left)>=noNodes(p.right)) 
					p = this.maxNode(p.left);
				else 
					p = this.minNode(p.right);
			}
			tmpNode.key = p.key;		
		}
		q = stack.peekFirst(); // 부모노드 저장 -> stack에서 뽑아내면 안되므로 peekFirst 사용
		deleteKeyP = q; // bf 계산하기위해 searchNode함수를 사용, 그 때 인자로 넣을 변수
										// maxNode, minNode의 부모 노드	
		
		// case of degree 0
		if(p.left == null && p.right == null) {
			if(q == null) this.root = null; //case of root
			else if(q.left == p) q.left = null;
			else q.right = null;
		}
		else{ // case of degree 1
			if(p.left != null) {
				if(q == null) this.root = T.left; // case of root
				else if(q.left == p) q.left = p.left;
				else q.right = p.left;
			}
			else {
				if(q == null) this.root = T.right; // case of root
				else if(q.left == p) q.left = p.right;
				else q.right = p.right;
			}
		}
		
		// update height
		while(!stack.isEmpty()) {
			q = stack.pop();
			q.height = updateHeight(q);
		}
		return 1;
	}
	
	int deleteAVL(Object newKey) { // 삭제 실패 == 0
		int d = deleteBST(this.root, newKey);
		if(d == 0) return d; // 키가 존재하지 않을 경우
		if(deleteKeyP != null) {
			searchNode(deleteKeyP.key); // deleteKeyP.key까지의 경로 스택 구성
			checkBalance(deleteKeyP.key);
		}
		if(this.rotationType != 0) rotateTree();
		return d;
	}
	
	String rtToString() { // rotationType String으로 변환 - 출력하기 위해 사용되는 함수
		String rotation = "";
		switch(this.rotationType) {
		case 0:
			rotation = "NO";
			break;
		case 1:
			rotation = "LL";
			break;
		case 2:
			rotation = "LR";
			break;
		case 3:
			rotation = "RR";
			break;
		case 4:
			rotation = "RL";
			break;
		}
		
		return rotation;
	}
	
	void inorderBST(TreeNode T) { // 순회
		if(T != null && T.key != null) {
			inorderBST(T.left);
			System.out.print("(" + T.key + ", " + T.bf + ") ");
			inorderBST(T.right);
		}
	}
	
	public static void main(String[] args) throws IOException {
		AVL test = new AVL();
		int a = 0;
		int b = 0;
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\alina\\Desktop\\Java\\File Processing\\src\\AVL-input.txt"));
		
		while(true) {
			String line = br.readLine();
			if(line == null) break;
			String[] llist = line.split(" ");
			
			if(llist[0].equals("i")) {
				a = test.insertAVL(Integer.parseInt(llist[1]));
				if(a != 0) System.out.print(test.rtToString() + " ");
			}
			else {
				b = test.deleteAVL(Integer.parseInt(llist[1]));
				if(b != 0) System.out.print(test.rtToString() + " ");
			}
			
			test.inorderBST(test.root);
			System.out.println();
		}
	}
}
