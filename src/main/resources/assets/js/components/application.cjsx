React = require 'react'
Staff = require './staff.cjsx'
store = require '../store.coffee'
actions = require '../actions.coffee'


DocumentSelector = React.createClass

    tryGo: (docId)->
        if (docId >= 0) && (docId < @state.nbDocs)
            actions.selectDoc.trigger docId
    
    goNext: ->
        @tryGo @state.docId + 1
        
    goPrevious: ->
        @tryGo @state.docId - 1
    
    componentDidMount: ->
        @unbinders = []
        onChangeDocId = => @setState {docId: store.getDoc().docId}
        onChangeNbDocs = =>
            @setState {nbDocs: store.getNbDocs()}
        @unbinders.push store.events.corpusChange.bind(onChangeNbDocs)
        @unbinders.push store.events.docChange.bind(onChangeDocId)
        $(document).on 'keydown', (e)=>
            if (e.which == 82)
                actions.reloadAnalyzer.trigger()
            if (e.which == 39)
                @goNext()
            if (e.which == 37)
                @goPrevious()
        
    componentWillUnmount: ->
        for unbinder in @unbinders
            @unbinder()

    getInitialState: ->
        {nbDocs: 1}
    
    render: ->
        <div>
            #{@state.docId} / #{@state.nbDocs}
        </div>


Application = React.createClass
    
    url: ->
        "http://localhost:8080/api/corpus/"

    docUrl: (docId)->
        "http://localhost:8080/api/corpus/#{docId}/processed"
    
    render: ->
        <div id="main">
            <div className='left-pane pane'>
                <DocumentSelector />
                <button onClick={actions.reloadAnalyzer.trigger}>reload</button>
            </div>
            
            <div className='right-pane pane'>
                <div className='search-bar'>
                    <input type='text'></input>
                </div>
                <Staff/>
            </div>
        </div>

module.exports = Application 