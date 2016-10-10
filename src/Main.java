public class Main {
	
	public static void main (String[] args)
	{
		BTree<Integer> tree = new BTree<>(8);
		tree.add(new Integer(8));
		tree.add(new Integer(5));
		tree.add(new Integer(6));
		tree.add(new Integer(21));
		tree.add(new Integer(0));
		tree.add(new Integer(11));
		tree.add(new Integer(112));
		tree.add(new Integer(12));
		tree.add(new Integer(1));
		tree.add(new Integer(2));

		System.out.println(tree.contains(new Integer(8)));
		System.out.println(tree.contains(new Integer(5)));
		System.out.println(tree.contains(new Integer(6)));
		System.out.println(tree.contains(new Integer(21)));
		System.out.println(tree.contains(new Integer(0)));
		System.out.println(tree.contains(new Integer(11)));
		System.out.println(tree.contains(new Integer(112)));
		System.out.println(tree.contains(new Integer(12)));
		System.out.println(tree.contains(new Integer(1)));
		System.out.println(tree.contains(new Integer(2)));

		System.out.println("-----------------------------");
		System.out.println(tree.contains(new Integer(-1)));
		System.out.println(tree.contains(new Integer(50)));
		System.out.println(tree.remove(new Integer(-1)));
		System.out.println(tree.remove(new Integer(50)));
		System.out.println(tree.remove(new Integer(51)));
		System.out.println("-----------------------------");

		System.out.println(tree.remove(new Integer(8)));
		System.out.println(tree.remove(new Integer(5)));
		System.out.println(tree.remove(new Integer(6)));
		System.out.println(tree.remove(new Integer(21)));
		System.out.println(tree.remove(new Integer(0)));
		System.out.println(tree.remove(new Integer(11)));
		System.out.println(tree.remove(new Integer(112)));
		System.out.println(tree.remove(new Integer(12)));
		System.out.println(tree.remove(new Integer(1)));
		System.out.println(tree.remove(new Integer(2)));

	}
}