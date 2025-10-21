# Experimental Grounds!

The views in this package (`ProjectDetailsView` and `ProjectListView`) are **experimental** in many aspects:

- They make heavy use of the signals, even though proper API support has not yet been added. Because of this, several
  workarounds have been used.
- They are built around the idea of having most of the UI state in separate View Models, that the UI
  components are then bound to.
- They mix signals and data providers, as we don't yet know how to do pagination with signals.
- They try a new way of constructing views that utilizes anonymous subclasses and initializer blocks. The idea is to
  make the code more declarative than imperative, but whether this actually makes the code more or less readable
  and understandable is not clear.

In other words, approach the classes in this package with curiosity, but **don't treat them as examples of best
practices**.
