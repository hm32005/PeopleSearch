<script>
var dataSource = new kendo.data.DataSource({
  data: '<books><book id="1"><title>Secrets of the JavaScript Ninja</title></book></books>',
  schema: {
    // specify the the schema is XML
    type: "xml",
    // the XML element which represents a single data record
    data: "/books/book",
    // define the model - the object which will represent a single data record
    model: {
      // configure the fields of the object
      fields: {
        // the "title" field is mapped to the text of the "title" XML element
        title: "title/text()",
        // the "id" field is mapped to the "id" attribute of the "book" XML
		// element
        id: "@id"
      }
    }
  }
});
dataSource.fetch(function() {
  var books = dataSource.data();
  console.log(books[0].title); // displays "Secrets of the JavaScript Ninja"
});
</script>