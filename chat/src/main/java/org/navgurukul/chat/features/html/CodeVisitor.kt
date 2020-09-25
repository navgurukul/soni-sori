package org.navgurukul.chat.features.html

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Code
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.IndentedCodeBlock

/**
 * This class is in charge of visiting nodes and tells if we have some code nodes (inline or block).
 */
class CodeVisitor : AbstractVisitor() {

    var codeKind: Kind = Kind.NONE
        private set

    override fun visit(fencedCodeBlock: FencedCodeBlock?) {
        if (codeKind == Kind.NONE) {
            codeKind = Kind.BLOCK
        }
    }

    override fun visit(indentedCodeBlock: IndentedCodeBlock?) {
        if (codeKind == Kind.NONE) {
            codeKind = Kind.BLOCK
        }
    }

    override fun visit(code: Code?) {
        if (codeKind == Kind.NONE) {
            codeKind = Kind.INLINE
        }
    }

    enum class Kind {
        NONE,
        INLINE,
        BLOCK
    }
}